package CommonPackage;

import java.io.Serializable;

/*
 * Class which represent move in the game (checker and queen)
 */
public class CheckersMove implements Serializable {

	private static final long serialVersionUID = 7989998894212425464L;
	private int moveFromRow, moveFromCol; // coordinates of the checker to be
											// moved
	private int moveToRow, moveToCol; // coordinates where the checker will be
										// moved
	/*
	 * flags prevents to make 2 moves (when first is not a beating and second
	 * may be)
	 */
	private boolean movePerformedByQueen = false;// flag to know when queen only
													// moves
	private boolean beatingPerformedByQueen = false;// flag to know when queen
													// beats opponent checker

	public boolean isBeatingPerformedByQueen() {
		return beatingPerformedByQueen;
	}

	public void setBeatingPerformedByQueen(boolean beatingPerformedByQueen) {
		this.beatingPerformedByQueen = beatingPerformedByQueen;
	}

	public boolean isMovePerformedByQueen() {
		return movePerformedByQueen;
	}

	public void setMovePerformedByQueen(boolean movePerformedByQueen) {
		this.movePerformedByQueen = movePerformedByQueen;
	}

	public boolean isMoveBeating() {// if standard checker makes a jump return
									// true
		return (moveFromCol - moveToCol == 2 || moveFromCol - moveToCol == -2);
	}

	public int getMoveFromRow() {
		return moveFromRow;
	}

	public void setMoveFromRow(int moveFromRow) {
		this.moveFromRow = moveFromRow;
	}

	public int getMoveFromCol() {
		return moveFromCol;
	}

	public void setMoveFromCol(int moveFromCol) {
		this.moveFromCol = moveFromCol;
	}

	public int getMoveToRow() {
		return moveToRow;
	}

	public void setMoveToRow(int moveToRow) {
		this.moveToRow = moveToRow;
	}

	public int getMoveToCol() {
		return moveToCol;
	}

	public void setMoveToCol(int moveToCol) {
		this.moveToCol = moveToCol;
	}

	public CheckersMove(int moveFromRow, int moveFromCol, int moveToRow, int moveToCol) {
		// Constructor. Just set the values of the instance variables.
		this.moveFromRow = moveFromRow;
		this.moveFromCol = moveFromCol;
		this.moveToRow = moveToRow;
		this.moveToCol = moveToCol;
	}

}
