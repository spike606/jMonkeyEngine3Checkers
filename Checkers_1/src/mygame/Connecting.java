package mygame;

import java.io.IOException;



import CommonPackageGame.*;
import com.jme3.network.Client;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Connecting extends Thread {

    //logger
    private static final Logger logger = Logger.getLogger(CheckersGame.class.getName());
    
    private static MessageFromClient messageToServer;
    private MessageFromServer messageFromServer;
    static boolean connectedToServer = false;
    private volatile boolean threadRunning = true;
    private static final int SERVER_PORT = 8901;
    private static final String HOST_NAME = "localhost";
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

            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
                connectedToServer = false;
                CheckersGame.window.startButton.setEnabled(true);
                CheckersGame.window.stopButton.setEnabled(false);
            }


            
            myClient.start();
            connectedToServer = true;

            while (connectedToServer) {
                
                GameFlowClient.setTryingToConnect(false);
                GameFlowClient.setResign(false);
                
//                if(firstMessageIn == true){
//                    
//                    
//                    
//                    firstMessageIn = false;
//                }
                
                if (GameFlowClient.isResign() == true ) {
                    sendMessageToServer(-1, -1, GameFlowClient.isResign());
                    CheckersGame.window.startButton.setEnabled(true);
                    CheckersGame.window.stopButton.setEnabled(false);
                    break;

                } else if ( messageFromServer.getWinner() != GameFlowClient.EMPTY) {
                    if (messageFromServer.getWinner() == GameFlowClient.getMyColor()) {
                        CheckersGame.window.startButton.setEnabled(true);
                        CheckersGame.window.stopButton.setEnabled(false);

                        break;
                    } else {
                        CheckersGame.window.startButton.setEnabled(true);
                        CheckersGame.window.stopButton.setEnabled(false);

                        break;
                    }
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

    }

    public static void sendMessageToServer(int row, int col, boolean resign) {
        prepareMessageToServer(row, col, resign);

        myClient.send(messageToServer);

    }

    @Override
    public void destroy() {
        myClient.close();
        super.destroy();
    }

    class ClientListener implements MessageListener<Client> {

    public void messageReceived(Client source, Message m) {
            if (m instanceof MessageFromServer) {
               
//                firstMessageIn = true;
                
                messageFromServer = (MessageFromServer) m;

                getDataFromServer(messageFromServer.getBoard(), messageFromServer.getChosenRow(),
                        messageFromServer.getChosenCol(), messageFromServer.isGameRunning(),
                        messageFromServer.getCurrentPlayer(), messageFromServer.getPossibleMoves(),
                        messageFromServer.getMyColor(), messageFromServer.getWinner());

                logger.log(Level.INFO, "Ch col: {0}", messageFromServer.getChosenCol());
                logger.log(Level.INFO, "Ch row: {0}", messageFromServer.getChosenRow());

            }
        }
    }
}
