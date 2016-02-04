package ServerPackage;

import CommonPackageServer.CheckersMove;
import CommonPackageServer.MessageFromClient;
import CommonPackageServer.MessageFromServer;
import com.jme3.network.Filters;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Server;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private boolean firstMessageOut = false;

    public Player(Server myServer, HostedConnection myHostedConnection, int color, Match match) {
        this.messageFromClient = new MessageFromClient();
        this.messageToClient = new MessageFromServer();
        this.myHostedConnection = myHostedConnection;
        this.myColor = color;
        this.match = match;
        this.myServer = myServer;
        this.myConnectionId = myHostedConnection.getId();
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
                myHostedConnection.getServer().broadcast(Filters.in(myHostedConnection), messageToClient);//send message to client

                while (true && threadRunning) {

                    if (match.gameFlow.getCurrentPlayer() == myColor && match.gameFlow.isGameRunning() && firstMessageOut == false) {

                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());
                        myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);

                        firstMessageOut = true;
                    }
                    if (!match.gameFlow.isGameRunning() && match.gameFlow.getWinner() > 0) //got winner
                    {
                        // prepare and send answer to client
                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());
                        myHostedConnection.getServer().broadcast(Filters.in(myHostedConnection), messageToClient);

                        threadRunning = false;

                    }

                    //when opponent is out you win
                    if (!myServer.getConnections().contains(opponentHostedConnection)) {
                        match.gameFlow.setWinner(getMyColor());
                        match.gameFlow.setCurrentPlayer(getMyColor());
                        match.gameFlow.setGameRunning(false);

                        // prepare and send answer to client
                        prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                                match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                                match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());
                        myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);
                        threadRunning = false;
                    }
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        logger.log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
        myHostedConnection.close("Client out");
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
        messageToClient.setReliable(true);

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

            // prepare and send answer to client
            prepareMessageToClient(match.gameFlow.boardData.getBoard(), match.gameFlow.getChosenCol(),
                    match.gameFlow.getChosenRow(), match.gameFlow.isGameRunning(), match.gameFlow.getCurrentPlayer(),
                    match.gameFlow.getPossibleMoves(), match.gameFlow.getWinner(), getMyColor());

            if (!match.gameFlow.isGameRunning() && match.gameFlow.getWinner() > 0) //got winner
            {
                myHostedConnection.getServer().broadcast(Filters.in(myHostedConnection), messageToClient);
                threadRunning = false;

            } else //I can still move so (beating) so send message to players to refresh view
            if (match.gameFlow.getCurrentPlayer() == getMyColor()) {

                //to me 
                myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);

                //to opponent - diffrent color
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

            } else {
                myHostedConnection.getServer().broadcast(Filters.equalTo(myHostedConnection), messageToClient);
            }
            firstMessageOut = false;
        }
    }

    private synchronized int getMyColor() {
        if (this.myColor == 1) {
            return 1;
        } else {
            return 3;
        }
    }
}
