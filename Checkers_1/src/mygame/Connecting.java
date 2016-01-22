package mygame;

import java.io.IOException;



import CommonPackageGame.*;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ErrorListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.kernel.ConnectorException;
import com.jme3.network.serializing.Serializer;
import gameUI.CheckersUI;
import java.net.ConnectException;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connecting extends Thread implements ErrorListener {

    //logger
    private static final Logger logger = Logger.getLogger(CheckersGame.class.getName());
    private static MessageFromClient messageToServer;
    private MessageFromServer messageFromServer;
    static boolean connectedToServer = false;
    private volatile boolean threadRunning = true;
    private static final int SERVER_PORT = 8901;
    private static final String HOST_NAME = "192.168.0.101";
//        private static final String HOST_NAME = "localhost";
    private static Client myClient;
    private static boolean firstMessageIn = false;//pomocnicza ustawiana gdy odbierzemy wiadomosc z serwera

    public Connecting() {
        messageToServer = new MessageFromClient();
        messageFromServer = new MessageFromServer();

        Serializer.registerClass(MessageFromClient.class);//konieczna serializacja wiadomosci
        Serializer.registerClass(MessageFromServer.class);
        Serializer.registerClass(CheckersMove.class);

    }

    @Override
    public void run() {
        while (threadRunning) {
            try {
                myClient = Network.connectToServer(HOST_NAME, SERVER_PORT);
                myClient.addMessageListener(new ClientListener(), MessageFromClient.class, MessageFromServer.class);
                myClient.addErrorListener(this);//by obsluzyc bledy zwiazane z zerwanym polaczeniem
                myClient.start();
                connectedToServer = true;
            } catch (ConnectException ex) {
                logger.log(Level.SEVERE, "CAN'T CONNECT TO SERVER!");
                connectedToServer = false;
                CheckersGame.window.startButton.setEnabled(true);
                CheckersGame.window.stopButton.setEnabled(false);
                CheckersGame.window.infoLabel.setText(CheckersGame.CANT_CONNECT);
                GameFlowClient.setTryingToConnect(false);
                threadRunning = false;

            } catch (IOException ex) {
                Logger.getLogger(Connecting.class.getName()).log(Level.SEVERE, null, ex);
            }





            while (connectedToServer) {

                GameFlowClient.setTryingToConnect(false);
                GameFlowClient.setResign(false);

//                if(firstMessageIn == true){
//                    
//                    
//                    
//                    firstMessageIn = false;
//                }

                if (GameFlowClient.isResign() == true) {
                    sendMessageToServer(-1, -1, GameFlowClient.isResign());
                    CheckersGame.window.startButton.setEnabled(true);
                    CheckersGame.window.stopButton.setEnabled(false);


                } else if (messageFromServer.getWinner() > 0) {
                    System.out.println("WINNER: " + messageFromServer.getWinner());
                    System.out.println("MY COLOR: " + GameFlowClient.getMyColor());


                    if (messageFromServer.getWinner() == GameFlowClient.getMyColor()) {
                        CheckersGame.window.startButton.setEnabled(true);
                        CheckersGame.window.stopButton.setEnabled(false);
                        CheckersGame.playWinner = true;
//                        CheckersGame.animInProgress = true;//by najpierw animacja sie zakonczyla a dopiero nastapil reset gry
                        CheckersGame.matchFinished = true;

                        System.out.println("wy");


                        break;
                    } else {
                        CheckersGame.window.startButton.setEnabled(true);
                        CheckersGame.window.stopButton.setEnabled(false);
                        CheckersGame.playLooser = true;
//                        CheckersGame.animInProgress = true;
                        CheckersGame.matchFinished = true;

                        System.out.println("prze");

                        break;
                    }
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

    private void setWinner(int winner, boolean gameRunning) {

        GameFlowClient.setWinner(winner);
        GameFlowClient.setGameRunning(gameRunning);

    }

    private static void prepareMessageToServer(int row, int col, boolean resign) {
        messageToServer.setChosenCol(col);
        messageToServer.setChosenRow(row);
        messageToServer.setResign(resign);

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
        System.out.println(" ZAMYKAM");

        super.destroy();
    }

    public void handleError(Object source, Throwable t) {
        if (t instanceof ConnectorException) {
            System.out.println(" error out: ");
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

//                firstMessageIn = true;

                messageFromServer = (MessageFromServer) m;
                if (messageFromServer.getWinner() > 0) {
                    setWinner(messageFromServer.getWinner(), messageFromServer.isGameRunning());
                } else {
                    getDataFromServer(messageFromServer.getBoard(), messageFromServer.getChosenRow(),
                            messageFromServer.getChosenCol(), messageFromServer.isGameRunning(),
                            messageFromServer.getCurrentPlayer(), messageFromServer.getPossibleMoves(),
                            messageFromServer.getMyColor(), messageFromServer.getWinner());
                }
                logger.log(Level.INFO, "Ch col: {0}", messageFromServer.getChosenCol());
                logger.log(Level.INFO, "Ch row: {0}", messageFromServer.getChosenRow());
                System.out.println("winner: " + messageFromServer.getWinner());
                System.out.println("my color: " + messageFromServer.getMyColor());
                System.out.println("current player: " + messageFromServer.getCurrentPlayer());



//                System.out.println("ARRAY FROM SERVER: ");
//
//                int array[][] = messageFromServer.getBoard();
//                for (int i = 0; i < array.length; i++) {
//                    for (int j = 0; j < array[i].length; j++) {
//                        System.out.print(array[i][j]);
//                    }
//                    System.out.println();
//                }
            }
        }
    }
}
