/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerPackage;

import CommonPackageServer.CheckersMove;
import CommonPackageServer.MessageFromClient;
import CommonPackageServer.MessageFromServer;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;

/**
 *
 * @author Krystus
 */
public class Player extends Thread implements MessageListener<HostedConnection> {

    private Match match;
    private int color;
    private HostedConnection hostedConnection;
    MessageFromServer messageToClient;
    MessageFromClient messageFromClient;
    private volatile boolean threadRunning = true;// flag to kill thread
    public boolean resign = false;// used when out or pressed stop
    private boolean firstMessageSend = false;

    public Player(HostedConnection hostedConnection, int color, Match match) {
        this.messageFromClient = new MessageFromClient();
        this.messageToClient = new MessageFromServer();
        this.hostedConnection = hostedConnection;
        this.color = color;
        this.match = match;

    }

    public void run() {
        while (threadRunning) {
//            try {
//                myInput = new ObjectInputStream(mySocket.getInputStream());
//                myOutput = new ObjectOutputStream(mySocket.getOutputStream());
//                myOutput.flush();
//            } catch (IOException e1) {
//
//                System.out.println("Player out!: " + e1);
//                resign = true;
//                gameFlow.makeClick(-1, -1, resign);
////					System.out.println("Player " + getMyColor() + " died. Match number: " + matchNumber);
//                threadRunning = false;
//            }

            if (resign != true) {
//                try {

                // initial message
                prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                        match.gameFlow.getChosenRow(), true, match.gameFlow.getCurrentPlayer(), match.gameFlow.getPossibleMoves(),
                        GameData.EMPTY, color);
//                    myOutput.writeObject(messageToClient);
                hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);

                while (true && threadRunning) {// TODO:??
                    if (match.gameFlow.getCurrentPlayer() == color && match.gameFlow.isGameRunning() && firstMessageSend == false) {
                System.out.println("W petli 1 ");

                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), color);
                        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
                        firstMessageSend = true;
//                        myOutput.reset();
//                        myOutput.writeObject(messageToClient);



                    } else if (!match.gameFlow.isGameRunning() && match.gameFlow.getWinner() != GameData.EMPTY) {// game
                        // end
                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), color);
                        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
                System.out.println("W petli 2 ");

//                            myOutput.reset();
//                            myOutput.writeObject(messageToClient);
                        threadRunning = false;// to kill current thread

                    }

//                    }
//                } catch (IOException e) {
//                    // when user is out, opponent wins
//                    resign = true;
//                    gameFlow.makeClick(-1, -1, resign);
//						System.out.println("Player " + getMyColor() + " died. Match number: " + matchNumber);
//                    threadRunning = false;

//                } catch (ClassNotFoundException e) {
//                    resign = true;
//                    gameFlow.makeClick(-1, -1, resign);
//						System.out.println("Class not found error. Player " + getMyColor() + " died. Match number: "
//								+ matchNumber);
//                    threadRunning = false;
//                } finally {
//                    try {
//                        myOutput.close();
//                        myInput.close();
//                    } catch (IOException e) {
//								System.out.println("Error during closing streams!");
//                    }
//                            System.out.println("W petli 3 ");

                }
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

    public void messageReceived(HostedConnection source, Message m) {


        // receive message from client
        messageFromClient = (MessageFromClient) m;

        // process message from client
        match.gameFlow.makeClick(messageFromClient.getChosenRow(), messageFromClient.getChosenCol(),
                messageFromClient.isResign());

        // prepare and send answer to client
        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), color);
//        myOutput.reset();
//        myOutput.writeObject(messageToClient);
        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
                        firstMessageSend = false;


    }
}
