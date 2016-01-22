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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Krystus
 */
public class Player extends Thread implements MessageListener<HostedConnection> {

    //logger
    private static final Logger logger = Logger.getLogger(Player.class.getName());
    private Match match;
    private int myColor;
    private int myConnectionId;
    private HostedConnection myHostedConnection;
    private HostedConnection opponentHostedConnection;
    private Server myServer;
    private MessageFromServer messageToClient;
    private MessageFromClient messageFromClient;
    private volatile boolean threadRunning = true;// flag to kill thread
    private boolean resign = false;// used when out or pressed stop
    private boolean firstMessageOut = false;//pomocnicza ustawiana gdy wyslana zostala wiado do klienta i czekamy na odp

    public Player(Server myServer, HostedConnection myHostedConnection, int color, Match match) {
        this.messageFromClient = new MessageFromClient();
        this.messageToClient = new MessageFromServer();
        this.myHostedConnection = myHostedConnection;
        this.myColor = color;
        this.match = match;
        this.myServer = myServer;

        this.myConnectionId = myHostedConnection.getId();
//         hostedConnection.getServer().addConnectionListener(this);


    }

    public HostedConnection getMyHostedConnection() {
        return myHostedConnection;
    }

    public void setMyHostedConnection(HostedConnection myHostedConnection) {
        this.myHostedConnection = myHostedConnection;
    }

    public HostedConnection getOpponentHostedConnection() {
        return opponentHostedConnection;
    }

    public void setOpponentHostedConnection(HostedConnection opponentHostedConnection) {
        this.opponentHostedConnection = opponentHostedConnection;
    }

    @Override
    public void run() {
        while (threadRunning) {
            if (resign != true) {

                // initial message
                prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                        match.gameFlow.getChosenRow(), true, match.gameFlow.getCurrentPlayer(), match.gameFlow.getPossibleMoves(),
                        GameData.EMPTY, getMyColor());
//                hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);//send message to client
                myHostedConnection.getServer().broadcast(Filters.in(myHostedConnection), messageToClient);//send message to client

//                        hostedConnection.send(messageToClient);

                while (true && threadRunning) {// TODO:??


                    checkResign(myHostedConnection);

                    if (match.gameFlow.getCurrentPlayer() == myColor && match.gameFlow.isGameRunning() && firstMessageOut == false) {

                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());
//                        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
                        myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);

//                                                hostedConnection.send(messageToClient);

                        firstMessageOut = true;
                    } else if (!match.gameFlow.isGameRunning() && match.gameFlow.getWinner() != GameData.EMPTY) {// game end
                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());
//                        hostedConnection.getServer().broadcast(Filters.in(hostedConnection), messageToClient);
                        myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);

//                        hostedConnection.send(messageToClient);
                        threadRunning = false;// to kill current thread

                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            }


        }


//        myHostedConnection.close("Client out");
//	System.out.println("Close 2");

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

        if (source == myHostedConnection && match.gameFlow.getCurrentPlayer() == myColor
                && match.gameFlow.isGameRunning() && firstMessageOut == true && m instanceof MessageFromClient) {

            // receive message from client
            messageFromClient = (MessageFromClient) m;

            // process message from client
            match.gameFlow.makeClick(messageFromClient.getChosenRow(), messageFromClient.getChosenCol(),
                    messageFromClient.isResign());
            System.out.println("RESIFN: " + messageFromClient.isResign());
            System.out.println("winner: " + match.gameFlow.getWinner());


            // prepare and send answer to client
            prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                    match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                    match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());

            System.out.println("curent player: " + match.gameFlow.getCurrentPlayer());
            System.out.println("curent player: " + getMyColor());

            if (!match.gameFlow.isGameRunning() && match.gameFlow.getWinner() > 0) //jesli jest zwyciezca to wyslij wiadomosc do obydwu graczy
            {
                myHostedConnection.getServer().broadcast(Filters.in(myHostedConnection, opponentHostedConnection), messageToClient);

            } else //jezeli dlej mam prawo ruchu tzn ze jest jeszce bicie wiec wyslij wiadomosc do obydwu graczy by uaktualnic widok
            if (match.gameFlow.getCurrentPlayer() == getMyColor()) {

                //do mnie 
                myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);

                //do przeciwnika - ze zmieniona wiadomoscia - ZMIANA KOLORU BO TO WIADOMOSC DO DRUGIEGO GRACZA!
                // prepare and send answer to client
                int color;
                if (getMyColor() == 1) {
                    color = 3;
                } else {
                    color = 1;
                }
                prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                        match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                        match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), color);
                myHostedConnection.getServer().broadcast(Filters.equalTo(opponentHostedConnection), messageToClient);


            } else {//jesli nie to tylkoo do jednego
                myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);

            }





//                                    hostedConnection.send(messageToClient);

            firstMessageOut = false;
        }

    }

    public void checkResign(HostedConnection conn) {
//        if (!myServer.getConnections().contains(conn)) {
//            resign = true;
//            match.gameFlow.makeClick(-1, -1, resign);
//            threadRunning = false;
//            
//                        // prepare and send answer to client - when resign
//            prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
//                    match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
//                    match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), myColor);
        //send to all
//            myHostedConnection.getServer().broadcast(Filters.in(myHostedConnection, opponentHostedConnection), messageToClient);
//        }
//System.out.println(Arrays.deepToString(match.gameFlow.boardData.getBoard()));
//        int[][] array = match.gameFlow.boardData.getBoard();
//        System.out.println();
//        System.out.println();
//
//        for (int i = 0; i < array.length; i++) {
//            for (int j = 0; j < array[i].length; j++) {
//                System.out.print(array[i][j]);
//            }
//            System.out.println();
//        }
//        System.out.println();
//        System.out.println();
    }

    private synchronized int getMyColor() {
        if (this.myColor == 1) {
            return 1;
        } else {
            return 3;
        }
    }
}
