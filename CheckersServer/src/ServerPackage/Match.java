package ServerPackage;

public class Match {

    int matchNumber;
    // objects to manage game for this match
    GameFlow gameFlow;
    private Player whitePlayer;
    private Player blackPlayer;

    public Match(int matchNumber) {

        this.matchNumber = matchNumber;

        this.gameFlow = new GameFlow();
        this.gameFlow.setGameRunning(true);
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
}
