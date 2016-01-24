package mygame;

import CommonPackageGame.*;

/*
 * Class used to manage the game flow, stores game data used on client side
 */
public class GameFlowClient {

	static boolean gameRunning = false;// flag
	// figures on the board
	static final int EMPTY = 0, WHITE = 1, WHITE_QUEEN = 2, BLACK = 3, BLACK_QUEEN = 4;
	static private int[][] board = new int[8][8];// array of current board state
													// - client side

	static CheckersMove[] possibleMoves;// array with possible moves for current
										// player

	static int currentPlayer;// contain current player (BLACK or WHITE)
	static int chosenRow = -1;// coordinates of selected checker
	static int chosenCol = -1;// -1 means no selected row or column
	static int myColor;
	static int winner = -1;
	static boolean resign = false;

	static boolean tryingToConnect = false;
	static Connecting connecting;

	public static boolean isTryingToConnect() {
		return tryingToConnect;
	}

	public static void setTryingToConnect(boolean tryingToConnect) {
		GameFlowClient.tryingToConnect = tryingToConnect;
	}

	public static boolean isResign() {
		return resign;
	}

	public static void setResign(boolean resign) {
		GameFlowClient.resign = resign;
	}

	public static int getWinner() {
		return winner;
	}

	public static void setWinner(int winner) {
		GameFlowClient.winner = winner;
	}

	public static int getMyColor() {
		return myColor;
	}
        public static String getMyColorString() {
            if(myColor == WHITE)
		return "White: ";
            else return "Black: ";
	}

	public static void setMyColor(int myColor) {
		GameFlowClient.myColor = myColor;
	}

	public static boolean isGameRunning() {
		return gameRunning;
	}

	public static void setGameRunning(boolean gameRunning) {
		GameFlowClient.gameRunning = gameRunning;
	}

	public static int getChosenRow() {
		return chosenRow;
	}

	public static void setChosenRow(int chosenRow) {
		GameFlowClient.chosenRow = chosenRow;
	}

	public static int getChosenCol() {
		return chosenCol;
	}

	public static void setChosenCol(int chosenCol) {
		GameFlowClient.chosenCol = chosenCol;
	}

	public static int getCurrentPlayer() {
		return currentPlayer;
	}

	public static void setCurrentPlayer(int currentPlayer) {
		GameFlowClient.currentPlayer = currentPlayer;
	}

	public static int[][] getBoard() {
		return board;
	}

	public static void setBoard(int[][] board) {
		GameFlowClient.board = board;
	}

	public static CheckersMove[] getPossibleMoves() {
		return possibleMoves;
	}

	public static void setPossibleMoves(CheckersMove[] possibleMoves) {
		GameFlowClient.possibleMoves = possibleMoves;
	}

	public GameFlowClient() {

		initializeGame();

	}

	/*
	 * Running when program starts, needed to draw view
	 */
	private void initializeGame() {
		setElementsOnStart();

	}

	/*
	 * Set up board on start
	 */
	public static void setElementsOnStart() {
		for (int row = 0; row < 8; row++) {
			for (int col = 0; col < 8; col++) {
				if (row % 2 != col % 2) {
					if (row < 3)
						board[row][col] = BLACK;
					else if (row > 4)
						board[row][col] = WHITE;
					else
						board[row][col] = EMPTY;
				} else {
					board[row][col] = EMPTY;
				}
			}
		}
	}

	// get current checker on selected field
	public static int getFieldOnBoard(int row, int col) {
		return board[row][col];
	}

	/*
	 * Performed after clicking button START
	 */
	public static void startNewGame() {

                CheckersGame.window.startButton.setEnabled(false);
		CheckersGame.window.stopButton.setEnabled(false);
                CheckersGame.window.infoLabel.setText(CheckersGame.CONNECTING);
                
                Connecting.firstMessageIn = false;
		connecting = new Connecting();
                            //register listener
		connecting.start();


	}

	/*
	 * Performed after clicking button STOP
	 */
	public static void resignGame() {
		resign = true;

	}

	
}
