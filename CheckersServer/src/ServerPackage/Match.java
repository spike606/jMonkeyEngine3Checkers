package ServerPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import CommonPackage.*;
import ServerPackage.Match.Player;
import com.jme3.network.HostedConnection;

public class Match {

    int matchNumber;
    // objects to manage game for this match
    GameFlow gameFlow;
    MessageFromServer messageToClient;
    MessageFromClient messageFromClient;

    public Match(int matchNumber) {

        this.matchNumber = matchNumber;

        gameFlow = new GameFlow();
        gameFlow.setGameRunning(true);

    }

    class Player extends Thread {

        private int myColor;
        private HostedConnection hostedConnection;
        private ObjectInputStream myInput;
        private ObjectOutputStream myOutput;
        private MessageFromClient messageFromClient = new MessageFromClient();
        private MessageFromServer messageToClient = new MessageFromServer();
        private volatile boolean threadRunning = true;// flag to kill thread
        public boolean resign = false;// used when out or pressed stop

        public Player(HostedConnection hostedConnection, int myColor) {

            this.hostedConnection = hostedConnection;
            this.myColor = myColor;

        }

        public void run() {
            while (threadRunning) {
//                try {
//					myInput = new ObjectInputStream(mySocket.getInputStream());
//					myOutput = new ObjectOutputStream(mySocket.getOutputStream());
//                    myOutput.flush();
//                } catch (IOException e1) {

//                    System.out.println("Player out!: " + e1);
//                    resign = true;
//                    gameFlow.makeClick(-1, -1, resign);
//					System.out.println("Player " + getMyColor() + " died. Match number: " + matchNumber);
//                    threadRunning = false;
//                }

//                if (resign != true) {
//                    try {
//
//                        // initial message
//                        prepareMessageToClient(gameFlow.boardData.getBoard(), gameFlow.getChosenCol(),
//                                gameFlow.getChosenRow(), true, gameFlow.getCurrentPlayer(), gameFlow.getPossibleMoves(),
//                                GameData.EMPTY, myColor);
//                        myOutput.writeObject(messageToClient);
//
//                        while (true && threadRunning) {// TODO:??
//                            if (gameFlow.getCurrentPlayer() == myColor && gameFlow.isGameRunning()) {
//
//                                prepareMessageToClient(gameFlow.boardData.getBoard(), gameFlow.getChosenCol(),
//                                        gameFlow.getChosenRow(), gameFlow.isGameRunning(), gameFlow.getCurrentPlayer(),
//                                        gameFlow.getPossibleMoves(), gameFlow.getWinner(), myColor);
//                                myOutput.reset();
//                                myOutput.writeObject(messageToClient);
//
//                                // receive message from client
//                                messageFromClient = (MessageFromClient) myInput.readObject();
//
//                                // process message from client
//                                gameFlow.makeClick(messageFromClient.getChosenRow(), messageFromClient.getChosenCol(),
//                                        messageFromClient.isResign());
//
//                                // prepare and send answer to client
//                                prepareMessageToClient(gameFlow.boardData.getBoard(), gameFlow.getChosenCol(),
//                                        gameFlow.getChosenRow(), gameFlow.isGameRunning(), gameFlow.getCurrentPlayer(),
//                                        gameFlow.getPossibleMoves(), gameFlow.getWinner(), myColor);
//                                myOutput.reset();
//                                myOutput.writeObject(messageToClient);
//
//                            } else if (!gameFlow.isGameRunning() && gameFlow.getWinner() != GameData.EMPTY) {// game
//                                // end
//                                prepareMessageToClient(gameFlow.boardData.getBoard(), gameFlow.getChosenCol(),
//                                        gameFlow.getChosenRow(), gameFlow.isGameRunning(), gameFlow.getCurrentPlayer(),
//                                        gameFlow.getPossibleMoves(), gameFlow.getWinner(), myColor);
//                                myOutput.reset();
//                                myOutput.writeObject(messageToClient);
//                                threadRunning = false;// to kill current thread
//
//                            }
//
//                        }
//                    } catch (IOException e) {
//                        // when user is out, opponent wins
//                        resign = true;
//                        gameFlow.makeClick(-1, -1, resign);
////						System.out.println("Player " + getMyColor() + " died. Match number: " + matchNumber);
//                        threadRunning = false;
//
//                    } catch (ClassNotFoundException e) {
//                        resign = true;
//                        gameFlow.makeClick(-1, -1, resign);
////						System.out.println("Class not found error. Player " + getMyColor() + " died. Match number: "
////								+ matchNumber);
//                        threadRunning = false;
//                    } finally {
//                        try {
//                            myOutput.close();
//                            myInput.close();
//                        } catch (IOException e) {
////								System.out.println("Error during closing streams!");
//                        }
//
//                    }
                }
            }
        }

        private void prepareMessageToClient(int[][] board, int chosenCol, int chosenRow, boolean gameRunning,
                int currentPlayer, CheckersMove[] possibleMoves, int winner, int myColor) {

            messageToClient.setBoard(board);
            messageToClient.setChosenCol(chosenCol);
            messageToClient.setChosenRow(chosenRow);
            messageToClient.setGameRunning(gameRunning);
            messageToClient.setCurrentPlayer(currentPlayer);
            messageToClient.setPossibleMoves(possibleMoves);
            messageToClient.setWinner(winner);
            messageToClient.setGameRunning(gameRunning);
            messageToClient.setMyColor(myColor);

        }

//        private synchronized String getMyColor() {
//            if (this.myColor == 1) {
//                return "WHITE";
//            } else {
//                return "BLACK";
//            }
//        }
//    }
}
