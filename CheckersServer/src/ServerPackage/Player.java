/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ServerPackage;

import CommonPackageServer.CheckersMove;
import CommonPackageServer.MessageFromClient;
import CommonPackageServer.MessageFromServer;
import com.jme3.network.ConnectionListener;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krystus
 */
public class Player extends Thread implements MessageListener<HostedConnection>, ConnectionListener {

    //logger
    private static final Logger logger = Logger.getLogger(Player.class.getName());
    private Match match;
    private int myColor;
    private HostedConnection hostedConnection;
    private MessageFromServer messageToClient;
    private MessageFromClient messageFromClient;
    private volatile boolean threadRunning = true;// flag to kill thread
    private boolean resign = false;// used when out or pressed stop
    private boolean firstMessageOut = false;//pomocnicza ustawiana gdy wyslana zostala wiado do klienta i czekamy na odp

    public Player(HostedConnection hostedConnection, int color, Match match) {
        this.messageFromClient = new MessageFromClient();
        this.messageToClient = new MessageFromServer();
        this.hostedConnection = hostedConnection;
        this.myColor = color;
        this.match = match;


    }

    @Override
    public void run() {
        while (threadRunning) {
            if (resign != true) {

                // initial message
                prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                        match.gameFlow.getChosenRow(), true, match.gameFlow.getCurrentPlayer(), match.gameFlow.getPossibleMoves(),
                        GameData.EMPTY, myColor);
                hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);//send message to client

                while (true && threadRunning) {// TODO:??
                    if (match.gameFlow.getCurrentPlayer() == myColor && match.gameFlow.isGameRunning() && firstMessageOut == false) {

                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), myColor);
                        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
                        firstMessageOut = true;
                    } else if (!match.gameFlow.isGameRunning() && match.gameFlow.getWinner() != GameData.EMPTY) {// game end
                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), myColor);
                        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);

                        threadRunning = false;// to kill current thread

                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
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

    //listener - when message is received
    public void messageReceived(HostedConnection source, Message m) {

        if (match.gameFlow.getCurrentPlayer() == myColor && match.gameFlow.isGameRunning() && firstMessageOut == true) {

            // receive message from client
            messageFromClient = (MessageFromClient) m;

            // process message from client
            match.gameFlow.makeClick(messageFromClient.getChosenRow(), messageFromClient.getChosenCol(),
                    messageFromClient.isResign());

            // prepare and send answer to client
            prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                    match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                    match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), myColor);

            hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
            firstMessageOut = false;
        }

    }

    public void connectionAdded(Server server, HostedConnection conn) {
    }

    public void connectionRemoved(Server server, HostedConnection conn) {

        resign = true;
        match.gameFlow.makeClick(-1, -1, resign);
        threadRunning = false;

        logger.log(Level.INFO, "Client out: {0}", conn.getId());
        conn.close("");
    }
}
