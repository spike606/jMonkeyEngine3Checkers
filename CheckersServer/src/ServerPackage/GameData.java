package ServerPackage;

import CommonPackageServer.CheckersMove;
import java.util.ArrayList;


/*
 * Class used to store data about board - pawns location etc
 */
public final class GameData {

    // figures on the board
    static final int EMPTY = 0, WHITE = 1, WHITE_QUEEN = 2, BLACK = 3, BLACK_QUEEN = 4;
    private int[][] board = new int[8][8];// array of current board state

    public int[][] getBoard() {
        return board;
    }

    public GameData() {

        setElementsOnStart();

    }

    /*
     * Set up board on start
     */
    public void setElementsOnStart() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (row % 2 != col % 2) {
                    if (row < 3) {
                        board[row][col] = BLACK;
                    } else if (row > 4) {
                        board[row][col] = WHITE;
                    } else {
                        board[row][col] = EMPTY;
                    }
                } else {
                    board[row][col] = EMPTY;
                }
            }
        }
    }

    // get current checker on selected field
    public int getFieldOnBoard(int row, int col) {
        return board[row][col];
    }

    /*
     * Make the move, move is not null and is legal.
     */
    public void makeMove(CheckersMove move) {

        /*
         * If move is performed by queen
         */
        if (board[move.getMoveFromRow()][move.getMoveFromCol()] == BLACK_QUEEN
                || board[move.getMoveFromRow()][move.getMoveFromCol()] == WHITE_QUEEN) {
            removeOpponentCheckerIfBeating(move);
            moveChecker(move);
        } /*
         * If move is performed by normal checker
         */ else if (move.isMoveBeating()) {// checker beats
            removeOpponentChecker(move);
            moveChecker(move);
        } else {
            moveChecker(move);// checker just moves
        }
        checkIfNewQueen(move);

    }

    private void removeOpponentCheckerIfBeating(CheckersMove move) {// checks
        // queens
        // move and
        // remove
        // checker
        // if it is
        // beating
        int opponentCheckerRow = 0;// Coordinates of possible opponent checker
        int opponentCheckerCol = 0;

        int checkRow = move.getMoveFromRow();// start checking from queen
        // location
        int checkCol = move.getMoveFromCol();

        /*
         * Start looking for opponent checker in 4 possible directions
         */
        if (move.getMoveFromRow() < move.getMoveToRow() && move.getMoveFromCol() < move.getMoveToCol()) {

            while (checkCol < move.getMoveToCol() && checkRow < move.getMoveToRow()) {
                checkCol++;
                checkRow++;

                if (board[checkRow][checkCol] != EMPTY) {
                    move.setBeatingPerformedByQueen(true);// set flags - beating
                    // true, just move -
                    // false
                    move.setMovePerformedByQueen(false);

                    break;
                }
            }

        } else if (move.getMoveFromRow() < move.getMoveToRow() && move.getMoveFromCol() > move.getMoveToCol()) {
            while (checkCol > move.getMoveToCol() && checkRow < move.getMoveToRow()) {
                checkCol--;
                checkRow++;

                if (board[checkRow][checkCol] != EMPTY) {
                    move.setBeatingPerformedByQueen(true);
                    move.setMovePerformedByQueen(false);

                    break;
                }
            }

        } else if (move.getMoveFromRow() > move.getMoveToRow() && move.getMoveFromCol() < move.getMoveToCol()) {
            while (checkCol < move.getMoveToCol() && checkRow > move.getMoveToRow()) {
                checkCol++;
                checkRow--;

                if (board[checkRow][checkCol] != EMPTY) {
                    move.setBeatingPerformedByQueen(true);
                    move.setMovePerformedByQueen(false);

                    break;
                }
            }

        } else if (move.getMoveFromRow() > move.getMoveToRow() && move.getMoveFromCol() > move.getMoveToCol()) {
            while (checkCol > move.getMoveToCol() && checkRow > move.getMoveToRow()) {

                checkCol--;
                checkRow--;

                if (board[checkRow][checkCol] != EMPTY) {
                    move.setBeatingPerformedByQueen(true);
                    move.setMovePerformedByQueen(false);

                    break;
                }
            }

        }

        opponentCheckerCol = checkCol;
        opponentCheckerRow = checkRow;

        board[opponentCheckerRow][opponentCheckerCol] = EMPTY;// remove opponent
        // checker

        if (move.isBeatingPerformedByQueen() == false) {
            move.setMovePerformedByQueen(true);
            // if not beating then queen is just moving, so it can't make
            // another beating
        }

    }

    /*
     * Creates new queen
     */
    private void checkIfNewQueen(CheckersMove move) {
        if (move.getMoveToRow() == 0 && board[move.getMoveToRow()][move.getMoveToCol()] == WHITE) {
            move.setMovePerformedByQueen(true);// to prevent beating by new
            // queen
            board[move.getMoveToRow()][move.getMoveToCol()] = WHITE_QUEEN;

        }
        if (move.getMoveToRow() == 7 && board[move.getMoveToRow()][move.getMoveToCol()] == BLACK) {
            move.setMovePerformedByQueen(true);// to prevent beating by new
            // queen
            board[move.getMoveToRow()][move.getMoveToCol()] = BLACK_QUEEN;

        }
    }

    /*
     * Make standard move in game data
     */
    private void moveChecker(CheckersMove move) {
        board[move.getMoveToRow()][move.getMoveToCol()] = board[move.getMoveFromRow()][move.getMoveFromCol()];
        board[move.getMoveFromRow()][move.getMoveFromCol()] = EMPTY;
    }

    /*
     * When beating is performed Remove beaten checker from the board
     */
    private void removeOpponentChecker(CheckersMove move) {

        int opponentCheckerRow = 0;
        int opponentCheckerCol = 0;

        if (move.getMoveFromRow() < move.getMoveToRow() && move.getMoveFromCol() < move.getMoveToCol()) {
            opponentCheckerRow = move.getMoveToRow() - 1;
            opponentCheckerCol = move.getMoveToCol() - 1;
        } else if (move.getMoveFromRow() < move.getMoveToRow() && move.getMoveFromCol() > move.getMoveToCol()) {
            opponentCheckerRow = move.getMoveToRow() - 1;
            opponentCheckerCol = move.getMoveToCol() + 1;
        } else if (move.getMoveFromRow() > move.getMoveToRow() && move.getMoveFromCol() < move.getMoveToCol()) {
            opponentCheckerRow = move.getMoveToRow() + 1;
            opponentCheckerCol = move.getMoveToCol() - 1;
        } else if (move.getMoveFromRow() > move.getMoveToRow() && move.getMoveFromCol() > move.getMoveToCol()) {
            opponentCheckerRow = move.getMoveToRow() + 1;
            opponentCheckerCol = move.getMoveToCol() + 1;
        }

        board[opponentCheckerRow][opponentCheckerCol] = EMPTY;

    }

    /*
     * Make an array containing possible moves
     */
    public CheckersMove[] getPossibleMovesForPlayer(int player) {

        if (player != WHITE && player != BLACK) {
            return null;
        }

        int playerQueen; // The constant representing a queen belonging to
        // player.
        if (player == WHITE) {
            playerQueen = WHITE_QUEEN;
        } else {
            playerQueen = BLACK_QUEEN;
        }

        // temporary array for possible moves
        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();

        // check possible beating - if so player must to beat
        checkPossibleBeating(moves, player, playerQueen);

        // if no beating is possible check for regular moves
        if (moves.size() == 0) {
            checkPossibleRegularMoves(moves, player, playerQueen);

        }

        // if no possible moves return null
        if (moves.size() == 0) {
            return null;

        } else {
            CheckersMove[] arrayOfPossibleMoves = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++) {
                arrayOfPossibleMoves[i] = moves.get(i);
            }
            return arrayOfPossibleMoves;

        }

    }

    /*
     * Because no beating is possible search for regular moves
     */
    private void checkPossibleRegularMoves(ArrayList<CheckersMove> moves, int player, int playerQueen) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player) {// check for possible regular
                    // move
                    // 4 directions
                    if (canMove(player, row, col, row + 1, col + 1)) {
                        moves.add(new CheckersMove(row, col, row + 1, col + 1));
                    }
                    if (canMove(player, row, col, row - 1, col + 1)) {
                        moves.add(new CheckersMove(row, col, row - 1, col + 1));
                    }
                    if (canMove(player, row, col, row + 1, col - 1)) {
                        moves.add(new CheckersMove(row, col, row + 1, col - 1));
                    }
                    if (canMove(player, row, col, row - 1, col - 1)) {
                        moves.add(new CheckersMove(row, col, row - 1, col - 1));
                    }
                } // check for possible moves if it is queen checker
                else if (board[row][col] == playerQueen) {
                    canQueenMove(moves, player, row, col);
                }
            }
        }
    }

    /*
     * Find regular moves for queen in 4 directions
     */
    private void canQueenMove(ArrayList<CheckersMove> moves, int player, int rowFrom, int colFrom) {

        int rowToCheck = rowFrom;// start from queen position
        int colToCheck = colFrom;

        // check for white or black player
        if (player == WHITE) {

            // first direction - from lower right corner to upper left corner
            while (--rowToCheck >= 0 && --colToCheck >= 0) {
                if (board[rowToCheck][colToCheck] != EMPTY) {
                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            // second direction - from lower left corner to upper right corner
            while (--rowToCheck >= 0 && ++colToCheck <= 7) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            // third direction - from upper right corner to lower left corner
            while (++rowToCheck <= 7 && --colToCheck >= 0) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            // fourth direction - from upper left corner to lower right corner
            while (++rowToCheck <= 7 && ++colToCheck <= 7) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }

        } else {
            // first direction - from lower right corner to upper left corner
            while (--rowToCheck >= 0 && --colToCheck >= 0) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            // second direction - from lower left corner to upper right corner
            while (--rowToCheck >= 0 && ++colToCheck <= 7) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            // third direction - from upper right corner to lower left corner
            while (++rowToCheck <= 7 && --colToCheck >= 0) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            // fourth direction - from upper left corner to lower right corner
            while (++rowToCheck <= 7 && ++colToCheck <= 7) {

                if (board[rowToCheck][colToCheck] != EMPTY) {

                    break;
                }
                moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

            }

        }

    }

    /*
     * Find regular moves for normal checkers - 1 field each direction
     */
    private boolean canMove(int player, int rowFrom, int colFrom, int rowTo, int colTo) {
        // out of board
        if (colTo > 7 || colTo < 0 || rowTo > 7 || rowTo < 0) {
            return false;
        }

        // rowTo, colTo are occupied by another checker
        if (board[rowTo][colTo] != EMPTY) {
            return false;
        }

        // check for white or black player
        if (player == WHITE) {
            if (rowTo > rowFrom) {
                return false;// regular white checker can only move up
            }
            return true; // can move
        } else {
            if (rowTo < rowFrom) {
                return false;// regular black checker can only move down
            }
            return true; // can move
        }
    }

    /*
     * Find possible beating moves
     */
    private void checkPossibleBeating(ArrayList<CheckersMove> moves, int player, int playerQueen) {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (board[row][col] == player) {// check for possible beating
                    // for checker
                    // 4 directions
                    if (canBeat(player, row, col, row + 1, col + 1, row + 2, col + 2)) {
                        moves.add(new CheckersMove(row, col, row + 2, col + 2));
                    }
                    if (canBeat(player, row, col, row - 1, col + 1, row - 2, col + 2)) {
                        moves.add(new CheckersMove(row, col, row - 2, col + 2));
                    }
                    if (canBeat(player, row, col, row + 1, col - 1, row + 2, col - 2)) {
                        moves.add(new CheckersMove(row, col, row + 2, col - 2));
                    }
                    if (canBeat(player, row, col, row - 1, col - 1, row - 2, col - 2)) {
                        moves.add(new CheckersMove(row, col, row - 2, col - 2));
                    }
                } // check for possible beating if it is queen checker
                else if (board[row][col] == playerQueen) {
                    canQueenBeat(moves, player, row, col);
                }

            }

        }
    }

    /*
     * Find possible beating moves for queen in 4 directions, any number of
     * fields
     */
    private void canQueenBeat(ArrayList<CheckersMove> moves, int player, int rowFrom, int colFrom) {

        int rowToCheck = rowFrom;
        int colToCheck = colFrom;

        boolean enemyCheckerFound = false;
        // check for white or black player
        if (player == WHITE) {

            // first direction - from lower right corner to upper left corner
            while (--rowToCheck >= 0 && --colToCheck >= 0) {
                // when find same checker stop searching
                if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    enemyCheckerFound = true;// found black checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));// can
                    // beat

                }

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            enemyCheckerFound = false;
            // second direction - from lower left corner to upper right corner
            while (--rowToCheck >= 0 && ++colToCheck <= 7) {
                if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    enemyCheckerFound = true;// found black checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            enemyCheckerFound = false;
            // third direction - from upper right corner to lower left corner
            while (++rowToCheck <= 7 && --colToCheck >= 0) {
                if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    enemyCheckerFound = true;// found black checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            enemyCheckerFound = false;
            // fourth direction - from upper left corner to lower right corner
            while (++rowToCheck <= 7 && ++colToCheck <= 7) {
                if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN)
                        && enemyCheckerFound == true) {// second
                    // enemy
                    // checker
                    // -
                    // stop
                    // searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    enemyCheckerFound = true;// found black checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }

        } else {// when BLACK player
            // first direction - from lower right corner to upper left corner
            while (--rowToCheck >= 0 && --colToCheck >= 0) {
                if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    enemyCheckerFound = true;// found white checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            enemyCheckerFound = false;
            // second direction - from lower left corner to upper right corner
            while (--rowToCheck >= 0 && ++colToCheck <= 7) {
                if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    enemyCheckerFound = true;// found white checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            enemyCheckerFound = false;
            // third direction - from upper right corner to lower left corner
            while (++rowToCheck <= 7 && --colToCheck >= 0) {
                if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    enemyCheckerFound = true;// found white checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }
            rowToCheck = rowFrom;
            colToCheck = colFrom;
            enemyCheckerFound = false;
            // fourth direction - from upper left corner to lower right corner
            while (++rowToCheck <= 7 && ++colToCheck <= 7) {
                if (board[rowToCheck][colToCheck] == BLACK || board[rowToCheck][colToCheck] == BLACK_QUEEN) {
                    break;
                } else if ((board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN)
                        && enemyCheckerFound == true) {// second enemy checker -
                    // stop searching
                    enemyCheckerFound = false;
                    break;
                } else if (board[rowToCheck][colToCheck] == WHITE || board[rowToCheck][colToCheck] == WHITE_QUEEN) {
                    enemyCheckerFound = true;// found white checker
                } else if (enemyCheckerFound == true && board[rowToCheck][colToCheck] == EMPTY) {
                    moves.add(new CheckersMove(rowFrom, colFrom, rowToCheck, colToCheck));

                }

            }

        }
    }

    /*
     * Check if checker can beat another checker - only forward
     */
    private boolean canBeat(int player, int rowFrom, int colFrom, int rowJumped, int colJumped, int rowTo, int colTo) {

        // out of board
        if (colTo > 7 || colTo < 0 || rowTo > 7 || rowTo < 0) {
            return false;

        }

        // rowTo, colTo are occupied by another checker
        if (board[rowTo][colTo] != EMPTY) {
            return false;

        }

        // check for white or black player
        if (player == WHITE) {
            if (rowTo > rowFrom && board[rowFrom][colFrom] == WHITE) {

                return false;// regular white checker can only move up

            }
            if (board[rowJumped][colJumped] != BLACK && board[rowJumped][colJumped] != BLACK_QUEEN) {

                return false;// no black checker to beat

            }
            return true; // can beat
        } else {
            if (rowTo < rowFrom && board[rowFrom][colFrom] == BLACK) {

                return false;// regular black checker can only move down

            }
            if (board[rowJumped][colJumped] != WHITE && board[rowJumped][colJumped] != WHITE_QUEEN) {

                return false;// no white checker to beat

            }
            return true; // can beat
        }

    }

    /*
     * If beating was performed check if another beating is possible
     */
    public CheckersMove[] getPossibleSecondBeating(int player, int rowFrom, int colFrom) {

        if (player != WHITE && player != BLACK) {
            return null;
        }

        int playerQueen; // The constant representing a queen belonging to the
        // player.
        if (player == WHITE) {
            playerQueen = WHITE_QUEEN;
        } else {
            playerQueen = BLACK_QUEEN;
        }

        // temporary array for possible moves
        ArrayList<CheckersMove> moves = new ArrayList<CheckersMove>();

        // check possible beating - if so player must to beat
        checkPossibleSecondBeating(moves, player, playerQueen, rowFrom, colFrom);

        // if no possible moves return null
        if (moves.size() == 0) {
            return null;

        } else {
            CheckersMove[] arrayOfSecondBeat = new CheckersMove[moves.size()];
            for (int i = 0; i < moves.size(); i++) {
                arrayOfSecondBeat[i] = moves.get(i);
            }
            return arrayOfSecondBeat;

        }

    }

    /*
     * Check if second beating is possible for given coordinates
     */
    private void checkPossibleSecondBeating(ArrayList<CheckersMove> moves, int player, int playerQueen, int row,
            int col) {

        if (board[row][col] == player) {// check for possible second beating
            // for checker
            // 4 directions
            if (canBeat(player, row, col, row + 1, col + 1, row + 2, col + 2)) {
                moves.add(new CheckersMove(row, col, row + 2, col + 2));
            }
            if (canBeat(player, row, col, row - 1, col + 1, row - 2, col + 2)) {
                moves.add(new CheckersMove(row, col, row - 2, col + 2));
            }
            if (canBeat(player, row, col, row + 1, col - 1, row + 2, col - 2)) {
                moves.add(new CheckersMove(row, col, row + 2, col - 2));
            }
            if (canBeat(player, row, col, row - 1, col - 1, row - 2, col - 2)) {
                moves.add(new CheckersMove(row, col, row - 2, col - 2));
            }
        } // check for possible beating if it is queen checker
        else if (board[row][col] == playerQueen) {
            canQueenBeat(moves, player, row, col);
        }

    }
}
