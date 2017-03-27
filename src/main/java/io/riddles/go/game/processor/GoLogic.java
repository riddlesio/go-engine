package io.riddles.go.game.processor;

import io.riddles.go.game.board.BoardOperations;
import io.riddles.go.game.board.GoBoard;
import io.riddles.go.game.move.GoMove;
import io.riddles.go.game.state.GoPlayerState;
import io.riddles.go.game.state.GoState;
import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.go.game.board.Point;
import io.riddles.javainterface.exception.InvalidMoveException;

/**
 * Created by joost on 3-7-16.
 */
public class GoLogic {

    private int mFoundLiberties; /* Used in recursive flood function */
    private int mNrAffectedFields;
    private boolean[][] mAffectedFields; /* For checking groups */
    private boolean[][] mCheckedFields; /* For checking groups */
    private Boolean mIsTerritory = false;


    public GoLogic() {
        mAffectedFields = new boolean[256][256]; /* This maximizes the board to 256x256 */
        mCheckedFields = new boolean[256][256];
    }

    /**
     * Takes a GoState and transforms it with a GoPlayerState.
     *
     * Return
     * Returns nothing, but transforms the given GoState.
     * @param GoState The initial state
     * @param GoPlayerState The state of the player
     * @return
     */
    public void transform(GoState state, GoPlayerState playerState) throws InvalidInputException {
        GoMove move = playerState.getMove();
        if (move.getException() == null) {
            switch(move.getMoveType()) {
                case PLACE:
                    transformPlaceMove(state, move, playerState.getPlayerId());
                    break;
                default:
                    break;
            }
        } else {
        }
    }

    /**
     * Takes a GoState and applies the move.
     *
     * Return
     * Returns nothing, but transforms the given GoState.
     * @param GoState The initial state
     * @param GoMove The move of the player
     * @return
     */
    private void transformPlaceMove(GoState state, GoMove move, int playerId) {
        GoBoard board = state.getBoard();

        Point point = move.getCoordinate();


        String[][] originalBoard = getBoardArray(board);

    /* Check legality of move */
        if (point.getX() > board.getWidth() || point.getY() > board.getHeight() || point.getX() < 0 || point.getY() < 0) { /* Move within range */
            move.setException(new InvalidMoveException("Move out of bounds"));
        }
        if (!board.getFieldAt(point).equals(GoBoard.EMPTY_FIELD)) { /*Field is not available */
            move.setException(new InvalidMoveException("Chosen position is already filled"));
        }
        //if (board.getFieldAt(point) < 0) { /* Check Ko Rule */
        //    move.setException(new InvalidMoveException("Violation of Ko Rule"));
        //}
        /* TODO: check Ko rule ?*/


        board.setFieldAt(point, String.valueOf(playerId));
        board.setLastPosition(point);


        int stonesTaken = checkCaptures(board, playerId);
        move.setStonesTaken(stonesTaken);


        if (!checkSuicideRule(board, point, String.valueOf(playerId))) { /* Check Suicide Rule */
            move.setException(new InvalidMoveException("Illegal Suicide Move"));

        }

        if (move.getException() != null) {
        /* Undo changes */
            board.initializeFromArray(originalBoard);
        }

        //updateTotalStonesTaken(move, stonesTaken);
        //updatePlayerScores();
        //updateBoardWithKo(2 - move + 1); // update for opponent
    }


    public String[][] getBoardArray(GoBoard board) {
        String[][] clone = new String[board.getWidth()][board.getHeight()];
        for(int y = 0; y < board.getHeight(); y++)
            for(int x = 0; x < board.getWidth(); x++)
                clone[x][y] = board.getFieldAt(new Point(x, y));
        return clone;
    }


    private Boolean hasNeighbors(GoBoard board, Point p) {
        if (p.x > 0 && !board.getFieldAt(new Point(p.x-1, p.y)).equals(GoBoard.EMPTY_FIELD)) return true;
        if (p.x < board.getWidth() - 1 	&& !board.getFieldAt(new Point(p.x+1, p.y)).equals(GoBoard.EMPTY_FIELD)) return true;
        if (p.y > 0 			&& !board.getFieldAt(new Point(p.x, p.y-1)).equals(GoBoard.EMPTY_FIELD)) return true;
        if (p.y < board.getHeight() - 1   && !board.getFieldAt(new Point(p.x, p.y+1)).equals(GoBoard.EMPTY_FIELD)) return true;
        return false;
    }

    /**
     * Check for captures stones or stone groups
     * @param args :
     * @return :
     */
    private int checkCaptures(GoBoard board, int playerId) {
        int stonesTaken = 0;
        for(int y = 0; y < board.getHeight(); y++) {
            for(int x = 0; x < board.getWidth(); x++) {
                Point point = new Point(x,y);
                String field = board.getFieldAt(point);
                if (!board.getFieldAt(point).equals(GoBoard.EMPTY_FIELD) && !field.equals(String.valueOf(playerId))) {
                    mFoundLiberties = 0;
                    boolean[][] mark = new boolean[board.getWidth()][board.getHeight()];
                    for (int tx = 0; tx < board.getHeight(); tx++) {
                        for (int ty = 0; ty < board.getWidth(); ty++) {
                            mAffectedFields[tx][ty] = false;
                            mark[tx][ty] = false;
                        }
                    }
                    flood(board, mark, point, board.getFieldAt(point), 0);
                    if (mFoundLiberties == 0) { /* Group starves */
                        for (int tx = 0; tx < board.getHeight(); tx++) {
                            for (int ty = 0; ty < board.getWidth(); ty++) {
                                if (mAffectedFields[tx][ty]) {
                                    board.setFieldAt(new Point(tx, ty), GoBoard.EMPTY_FIELD);
                                    stonesTaken++;
                                }
                            }
                        }
                    }
                }
            }
        }
        return stonesTaken;
    }



    /**
     * Checks the Suicide Rule. (A move which creates a group that immediately has no liberties)
     * @param args : int x, int y, int move
     * @return : true if legal move otherwise false
     */
    private Boolean checkSuicideRule(GoBoard board, Point p, String move) {
        mFoundLiberties = 0;
        boolean[][] mark = new boolean[board.getWidth()][board.getHeight()];
        for (int tx = 0; tx < board.getWidth(); tx++) {
            for (int ty = 0; ty < board.getHeight(); ty++) {
                mAffectedFields[tx][ty] = false;
                mark[tx][ty] = false;
            }
        }
        flood(board, mark, p, move, 0);
        return (mFoundLiberties > 0);
    }


    /**
     * Checks whether there is any move available on the field
     * @param args :
     * @return : Returns true when there is no move available, otherwise returns false.
     */
    public boolean isBoardFull(GoBoard board) {
        for(int y = 0; y < board.getHeight(); y++)
            for(int x = 0; x < board.getWidth(); x++)
                for (int playerId = 1; playerId <= 2; playerId++)
                    if (board.getFieldAt(new Point(x,y)).equals(GoBoard.EMPTY_FIELD) && checkSuicideRule(board, new Point(x,y), String.valueOf(playerId)))
                        return false;
        // No move can be played
        return true;
    }

    /**
     * Returns player score according to Tromp-Taylor Rules
     * @param args : int playerId
     * @return : int player score
     */
    public int calculateScore(GoBoard board, int playerId) {
        int score = this.getPlayerStones(board, playerId);

        if (score <= 0) return 0;
        //System.out.println("getPlayerStones" + playerId + " " + this.getPlayerStones(board, 2 - playerId + 1));
        if (this.getPlayerStones(board, 2 - playerId + 1) == 0) { // opponent stones == 0
            if (score <= 1) return score;
            return board.getWidth() * board.getHeight();
        }

        /* Add empty points that reach only playerId color */
        boolean[][] mark = new boolean[board.getHeight()][board.getWidth()];
        mIsTerritory = false; mNrAffectedFields = 0;
        for(int y = 0; y < board.getHeight(); y++) {
            for(int x = 0; x < board.getWidth(); x++) {
                mCheckedFields[x][y] = false;
            }
        }


        for(int y = 0; y < board.getHeight(); y++) {
            for(int x = 0; x < board.getWidth(); x++) {
                Point point = new Point(x, y);
                if (board.getFieldAt(point).equals(GoBoard.EMPTY_FIELD) && mCheckedFields[x][y] == false) {
                    for (int tx = 0; tx < board.getHeight(); tx++) {
                        for (int ty = 0; ty < board.getWidth(); ty++) {
                            mAffectedFields[tx][ty] = false;
                            mark[tx][ty] = false;

                        }
                    }

                    mIsTerritory = true;
                    mNrAffectedFields = 0;
                    floodFindTerritory(board, mark, point, String.valueOf(playerId), 0);

                    if (mIsTerritory) {
                        score += mNrAffectedFields;
                        for (int tx = 0; tx < board.getHeight(); tx++) {
                            for (int ty = 0; ty < board.getWidth(); ty++) {
                                if (mAffectedFields[tx][ty]) {
                                    mCheckedFields[tx][ty] = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return score;
    }

    /**
     * Gets the amount of stones the given player has on the board
     * @param value : player id
     * @return : amount of stones on the board
     */
    public int getPlayerStones(GoBoard board, int value) {
        int stones = 0;
        for(int y = 0; y < board.getHeight(); y++)
            for(int x = 0; x < board.getWidth(); x++)
                if (board.getFieldAt(new Point(x, y)).equals(String.valueOf(value)))
                    stones++;
        return stones;
    }
    /**
     * Recursive function to check stone group liberties
     * @param args :
     * @return :
     */
    private void flood(GoBoard board, boolean [][]mark, Point p, String srcColor, int stackCounter) {
        // Make sure row and col are inside the board
        if (p.x < 0) return;
        if (p.y < 0) return;
        if (p.x >= board.getWidth()) return;
        if (p.y >= board.getHeight()) return;

        // Make sure this field hasn't been visited yet
        if (mark[p.x][p.y]) return;

        // Make sure this field is the right color to fill
        if (!board.getFieldAt(p).equals(srcColor)) {
            if (board.getFieldAt(p).equals(GoBoard.EMPTY_FIELD)) { mFoundLiberties++; } /* TODO: check Ko */
            return;
        }

        // Fill field with target color and mark it as visited
        mAffectedFields[p.x][p.y] = true;
        mark[p.x][p.y] = true;

        // Recursively check surrounding fields
        if (stackCounter < 1024) {
            flood(board, mark, new Point(p.x - 1, p.y), srcColor, stackCounter+1);
            flood(board, mark, new Point(p.x + 1, p.y), srcColor, stackCounter+1);
            flood(board, mark, new Point(p.x, p.y - 1), srcColor, stackCounter+1);
            flood(board, mark, new Point(p.x, p.y + 1), srcColor, stackCounter+1);
        }
    }


    /**
     * Recursive function to check stone group liberties.
     * Sets mIsTerritory member variable with result.
     * @param args :
     * @return :
     */
    private void floodFindTerritory(GoBoard board, boolean [][]mark, Point p, String srcColor, int stackCounter) {
    /* Strategy:
     * If edge other than (playerid or 0 or board edge) has been found, then no territory.
     */
        // Make sure row and col are inside the board
        if (p.x < 0) return;
        if (p.y < 0) return;
        if (p.x >= board.getWidth()) return;
        if (p.y >= board.getHeight()) return;

        // Make sure this field hasn't been visited yet
        if (mark[p.x][p.y]) return;

        // Make sure this field is the right color to fill
        if (!board.getFieldAt(p).equals(GoBoard.EMPTY_FIELD)) {
            if (!board.getFieldAt(p).equals(srcColor)) {
                mIsTerritory = false;
            }
            return;
        }

        mAffectedFields[p.x][p.y] = true;

        // Mark field as visited
        mNrAffectedFields++;
        mark[p.x][p.y] = true;

        if (stackCounter < 1024) {
            floodFindTerritory(board, mark, new Point(p.x - 1, p.y), srcColor, stackCounter+1);
            floodFindTerritory(board, mark, new Point(p.x + 1, p.y), srcColor, stackCounter+1);
            floodFindTerritory(board, mark, new Point(p.x, p.y - 1), srcColor, stackCounter+1);
            floodFindTerritory(board, mark, new Point(p.x, p.y + 1), srcColor, stackCounter+1);
        }
    }

    public boolean detectKo(GoState state) {
        String[][] originalBoard = getBoardArray(state.getBoard());
        String[][] middleBoard;
        String[][] compareBoard;

        if (state.hasPreviousState()) {
            GoState middleState = (GoState)state.getPreviousState();
            middleBoard = getBoardArray(middleState.getBoard());
            if (state.getPreviousState().hasPreviousState()) {
                GoState compareState = (GoState)middleState.getPreviousState();
                compareBoard = getBoardArray(compareState.getBoard());
                BoardOperations boardOps = new BoardOperations();
                if (boardOps.compareFields(originalBoard, compareBoard) && !boardOps.compareFields(originalBoard, middleBoard)) {
                    return true;
                }
            }
        }
        return false;
    }
}