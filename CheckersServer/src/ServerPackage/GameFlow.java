package ServerPackage;

import CommonPackageServer.CheckersMove;

/*
 * Class used to manage the game flow
 */
public class GameFlow {

    private boolean gameRunning = false;// flag
    private int winner = GameData.EMPTY;
    GameData boardData;// object containing current data of board
    private int currentPlayer;// BLACK or WHITE
    private int chosenRow = -1;// coordinates of selected checker
    private int chosenCol = -1;// -1 means no selected row or column
    CheckersMove[] possibleMoves;// array with possible moves for current
    // player

    synchronized int getChosenRow() {
        return chosenRow;
    }

    synchronized int getCurrentPlayer() {
        return currentPlayer;
    }

    synchronized void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    synchronized void setChosenRow(int chosenRow) {
        this.chosenRow = chosenRow;
    }

    synchronized int getChosenCol() {
        return chosenCol;
    }

    synchronized void setChosenCol(int chosenCol) {
        this.chosenCol = chosenCol;
    }

    synchronized CheckersMove[] getPossibleMoves() {
        return possibleMoves;
    }

    synchronized int getWinner() {
        return winner;
    }

    synchronized boolean isGameRunning() {
        return gameRunning;
    }

    synchronized void setGameRunning(boolean gameRunning) {
        this.gameRunning = gameRunning;
    }

    synchronized void setWinner(int winner) {
        this.winner = winner;
    }

    public GameFlow() {

        initializeGame();

    }

    private void initializeGame() {
        if (gameRunning == true) {
            // This should not be possible
            return;
        }
        boardData = new GameData();
        currentPlayer = GameData.WHITE;// white starts a game
        possibleMoves = boardData.getPossibleMovesForPlayer(currentPlayer);
        gameRunning = true;

    }

    private void gameIsOver(int winner) {
        gameRunning = false;
        this.winner = winner;
    }

    /*
     * Handle Click on board depending of current game state
     */
    synchronized void makeClick(int row, int col, boolean resign) {

        if (resign == true) {// when player resign

            if (currentPlayer == GameData.WHITE) {
                gameIsOver(GameData.BLACK);
            } else {
                gameIsOver(GameData.WHITE);
            }

        } else {
            /*
             * When no piece is selected Choose piece to move and save
             * coordinates to the class fields
             */
            for (int i = 0; i < possibleMoves.length; i++) {
                if (possibleMoves[i].getMoveFromRow() == row && possibleMoves[i].getMoveFromCol() == col) {
                    chosenRow = row;
                    chosenCol = col;
                    return;
                }
            }
            /*
             * When piece is not selected
             */
            if (chosenRow < 0) {
                return;
            }
            /*
             * Make move from selected field to another
             */
            for (int i = 0; i < possibleMoves.length; i++) {
                if (possibleMoves[i].getMoveFromRow() == chosenRow && possibleMoves[i].getMoveFromCol() == chosenCol
                        && possibleMoves[i].getMoveToRow() == row && possibleMoves[i].getMoveToCol() == col) {
                    performMove(possibleMoves[i]);
                    return;
                }
            }
        }
    }

    /*
     * Make specific move
     */
    synchronized private void performMove(CheckersMove checkerMove) {

        // make a move
        boardData.makeMove(checkerMove);

        /*
         * Check if second move is possible - when checker is beating and it is
         * not move performed by queen or when queen is beating
         */
        if ((checkerMove.isMoveBeating() && !checkerMove.isMovePerformedByQueen())
                || checkerMove.isBeatingPerformedByQueen()) {
            possibleMoves = boardData.getPossibleSecondBeating(currentPlayer, checkerMove.getMoveToRow(),
                    checkerMove.getMoveToCol());
            if (possibleMoves != null) {
                return;
            }
        }

        // restore default values
        checkerMove.setMovePerformedByQueen(false);
        checkerMove.setBeatingPerformedByQueen(false);

        /*
         * Change player and get moves for him. If possible moves are beating,
         * set info. Check if game is over.
         */
        if (currentPlayer == GameData.WHITE) {
            currentPlayer = GameData.BLACK;
            possibleMoves = boardData.getPossibleMovesForPlayer(currentPlayer);
            if (possibleMoves == null) {
                gameIsOver(GameData.WHITE);
                return;
            }

        } else {
            currentPlayer = GameData.WHITE;
            possibleMoves = boardData.getPossibleMovesForPlayer(currentPlayer);
            if (possibleMoves == null) {
                gameIsOver(GameData.BLACK);
                return;
            }

        }
        /*
         * Set default values - player has not yet selected a checker to move
         */
        chosenRow = -1;
        chosenCol = -1;
    }
}
