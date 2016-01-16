package ServerPackage;

import CommonPackage.*;

public class Match {

    int matchNumber;
    // objects to manage game for this match
    GameFlow gameFlow;
//    MessageFromServer messageToClient;
//    MessageFromClient messageFromClient;
    private Player whitePlayer;
    private Player blackPlayer;

    public Match(int matchNumber) {

        this.matchNumber = matchNumber;

        this.gameFlow = new GameFlow();
        this.gameFlow.setGameRunning(true);
//        this.messageFromClient = new MessageFromClient();
//        this.messageToClient = new MessageFromServer();

    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public void setWhitePlayer(Player whitePlayer) {
        this.whitePlayer = whitePlayer;
    }

    public Player getBlackPlayer() {
        return blackPlayer;
    }

    public void setBlackPlayer(Player blackPlayer) {
        this.blackPlayer = blackPlayer;
    }

//    public MessageFromServer getMessageToClient() {
//        return messageToClient;
//    }
//
//    public void setMessageToClient(MessageFromServer messageToClient) {
//        this.messageToClient = messageToClient;
//    }
//
//    public MessageFromClient getMessageFromClient() {
//        return messageFromClient;
//    }
//
//    public void setMessageFromClient(MessageFromClient messageFromClient) {
//        this.messageFromClient = messageFromClient;
//    }
    
}
