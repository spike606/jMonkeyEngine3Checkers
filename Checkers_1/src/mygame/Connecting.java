package mygame;

import java.io.IOException;
import CommonPackageGame.*;
import com.jme3.network.Client;
import com.jme3.network.ErrorListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.kernel.ConnectorException;
import com.jme3.network.serializing.Serializer;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connecting extends Thread implements ErrorListener {

    //logger
    private static final Logger logger = Logger.getLogger(CheckersGame.class.getName());
    private static MessageFromClient messageToServer;
    private MessageFromServer messageFromServer;
    public volatile static boolean connectedToServer = false;
    private volatile boolean threadRunning = true;
    private static final int SERVER_PORT = 8902;
    private static final String HOST_NAME = "192.168.0.101";
    public static Client myClient;
    public static boolean firstMessageIn = false;//set when received first message from server

    public Connecting() {
        messageToServer = new MessageFromClient();
        messageFromServer = new MessageFromServer();

        Serializer.registerClass(MessageFromClient.class);//serialization required by jme3
        Serializer.registerClass(MessageFromServer.class);
        Serializer.registerClass(CheckersMove.class);

    }

    @Override
    public void run() {
        while (threadRunning) {
            try {
                myClient = Network.connectToServer(HOST_NAME, SERVER_PORT);
                myClient.addMessageListener(new ClientListener(), MessageFromClient.class, MessageFromServer.class);
                myClient.addErrorListener(this);
                myClient.start();
                connectedToServer = true;
                CheckersGame.window.stopButton.setEnabled(true);

            } catch (ConnectException ex) {
                logger.log(Level.SEVERE, "CAN'T CONNECT TO SERVER!");
                connectedToServer = false;
                CheckersGame.window.startButton.setEnabled(true);
                CheckersGame.window.stopButton.setEnabled(false);
                CheckersGame.window.infoLabel.setText(CheckersGame.CANT_CONNECT);
                GameFlowClient.setTryingToConnect(false);
                threadRunning = false;

            } catch (IOException ex) {
                logger.log(Level.SEVERE, "CAN'T CONNECT TO SERVER!");
                connectedToServer = false;
                CheckersGame.window.startButton.setEnabled(true);
                CheckersGame.window.stopButton.setEnabled(false);
                CheckersGame.window.infoLabel.setText(CheckersGame.CANT_CONNECT);
                GameFlowClient.setTryingToConnect(false);
                threadRunning = false;
            }
            
            while (connectedToServer) {
                GameFlowClient.setTryingToConnect(false);
                GameFlowClient.setResign(false);

                if (GameFlowClient.isResign() == true) {
                    sendMessageToServer(-1, -1, GameFlowClient.isResign());
                    CheckersGame.window.startButton.setEnabled(true);
                    CheckersGame.window.stopButton.setEnabled(false);

                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Connecting.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
            threadRunning = false;
        }

    }

    private void getDataFromServer(int[][] board, int chosenRow, int chosenCol, boolean gameRunning, int currentPlayer,
            CheckersMove[] possibleMoves, int myColor, int winner) {
        GameFlowClient.setBoard(board);
        GameFlowClient.setChosenRow(chosenRow);
        GameFlowClient.setChosenCol(chosenCol);
        GameFlowClient.setGameRunning(gameRunning);
        GameFlowClient.setCurrentPlayer(currentPlayer);
        GameFlowClient.setPossibleMoves(possibleMoves);
        GameFlowClient.setMyColor(myColor);
        GameFlowClient.setWinner(winner);

    }

    private static void prepareMessageToServer(int row, int col, boolean resign) {
        messageToServer.setChosenCol(col);
        messageToServer.setChosenRow(row);
        messageToServer.setResign(resign);
        messageToServer.setReliable(true);

    }

    public static void sendMessageToServer(int row, int col, boolean resign) {
        prepareMessageToServer(row, col, resign);

        if ((GameFlowClient.gameRunning && GameFlowClient.getMyColor() == GameFlowClient.getCurrentPlayer()
                && CheckersGame.animInProgress == false)) {
            myClient.send(messageToServer);
        }

    }

    @Override
    public void destroy() {
        myClient.close();
        super.destroy();
    }

    public void handleError(Object source, Throwable t) {
        if (t instanceof ConnectorException) {
            GameFlowClient.resignGame();
            CheckersGame.matchFinished = true;
            GameFlowClient.setGameRunning(false);
            myClient.close();
            CheckersGame.window.startButton.setEnabled(true);
            CheckersGame.window.stopButton.setEnabled(false);
            CheckersGame.window.infoLabel.setText(CheckersGame.CANT_CONNECT);

        }

    }

    class ClientListener implements MessageListener<Client> {

        public void messageReceived(Client source, Message m) {
            if (m instanceof MessageFromServer) {

                firstMessageIn = true;

                messageFromServer = (MessageFromServer) m;

                getDataFromServer(messageFromServer.getBoard(), messageFromServer.getChosenRow(),
                        messageFromServer.getChosenCol(), messageFromServer.isGameRunning(),
                        messageFromServer.getCurrentPlayer(), messageFromServer.getPossibleMoves(),
                        messageFromServer.getMyColor(), messageFromServer.getWinner());

                if (messageFromServer.getWinner() > 0) {
                    if (messageFromServer.getWinner() == GameFlowClient.getMyColor()) {
                        CheckersGame.window.startButton.setEnabled(true);
                        CheckersGame.window.stopButton.setEnabled(false);
                        CheckersGame.playWinner = true;
                        CheckersGame.matchFinished = true;
                        CheckersGame.lastMove = true;
                    } else {
                        CheckersGame.window.startButton.setEnabled(true);
                        CheckersGame.window.stopButton.setEnabled(false);
                        CheckersGame.playLooser = true;
                        CheckersGame.matchFinished = true;
                        CheckersGame.lastMove = true;
                    }
                       connectedToServer = false;
                       CheckersGame.animInProgress = false;
                       Connecting.firstMessageIn = false;
                }
            }
        }
    }
}
