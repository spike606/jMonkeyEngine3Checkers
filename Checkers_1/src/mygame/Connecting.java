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

    private static MessageFromClient messageToServer;
    private MessageFromServer messageFromServer;
    static boolean connectedToServer = true;
    private volatile boolean threadRunning = true;
    private static final int SERVER_PORT = 8901;
    private static final String HOST_NAME = "localhost";
    Client myClient;

    public Connecting() {
        messageToServer = new MessageFromClient();
        Serializer.registerClass(MessageFromClient.class);//konieczna serializacja wiadomosci
        Serializer.registerClass(MessageFromServer.class);
        Serializer.registerClass(CheckersMove.class);



    }

    @Override
    public void run() {
        while (threadRunning) {
//			try {
            // Setup networking




            try {
                myClient = Network.connectToServer(HOST_NAME, SERVER_PORT);
                myClient.addMessageListener(new ClientListener(), MessageFromClient.class, MessageFromServer.class);

            } catch (IOException ex) {
                Logger.getLogger(CheckersGame.class.getName()).log(Level.SEVERE, null, ex);
                GameFlowClient.setTryingToConnect(false);
                connectedToServer = false;
                CheckersGame.window.startButton.setEnabled(true);
                CheckersGame.window.stopButton.setEnabled(false);
            }
            myClient.start();
            connectedToServer = true;
//				mySocket = new Socket(HOST_NAME, SERVER_PORT);
//				myOutput = new ObjectOutputStream(mySocket.getOutputStream());
//				myOutput.flush();
//				myInput = new ObjectInputStream(mySocket.getInputStream());

//			} catch (IOException e1) {
            // System.out.println("IOException1.");

//			}

            while (connectedToServer) {
//                try {
                            //TODO!!!!!!!!!!!!!!!!!!!!1
                if (GameFlowClient.isResign() == true) {
                    sendMessageToServer(-1, -1, GameFlowClient.isResign());
                    CheckersGame.window.startButton.setEnabled(true);
                    CheckersGame.window.stopButton.setEnabled(false);
                    break;

                } else if (messageFromServer.getWinner() != GameFlowClient.EMPTY) {
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

//                } catch (ClassNotFoundException e) {
//                    // System.out.println("Class not found.");
//                } catch (IOException e) {
//                    // System.out.println("IOException2.");
//                }

            }
            threadRunning = false;
//            try {
//                myOutput.close();
//                myInput.close();
//                mySocket.close();
//            } catch (IOException e) {
//                // System.out.println("Error during closing streams!");
//            }
        }

    }

    public Client getMyClient() {
        return myClient;
    }

    public void setMyClient(Client myClient) {
        this.myClient = myClient;
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

    public void sendMessageToServer(int row, int col, boolean resign) {
        prepareMessageToServer(row, col, resign);
//        try {
//            myOutput.reset();
//            myOutput.writeObject(messageToServer);
        myClient.send(messageToServer);
//        } catch (IOException e) {
        // e.printStackTrace();
//        }

    }

    @Override
    public void destroy() {
        myClient.close();
        super.destroy();
    }

    class ClientListener implements MessageListener<Client> {

        public void messageReceived(Client source, Message m) {
            if (m instanceof MessageFromServer) {

//                            object = myInput.readObject();
                messageFromServer = (MessageFromServer) m;

                GameFlowClient.setTryingToConnect(false);
                GameFlowClient.setResign(false);

                getDataFromServer(messageFromServer.getBoard(), messageFromServer.getChosenRow(),
                        messageFromServer.getChosenCol(), messageFromServer.isGameRunning(),
                        messageFromServer.getCurrentPlayer(), messageFromServer.getPossibleMoves(),
                        messageFromServer.getMyColor(), messageFromServer.getWinner());
                System.out.println("Ch col:" + messageFromServer.getChosenCol());
                System.out.println("Ch row:" + messageFromServer.getChosenRow());





            }
        }
    }
}
