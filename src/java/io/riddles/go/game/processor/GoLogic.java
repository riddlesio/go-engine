/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.go.game.processor;

import io.riddles.go.game.board.BoardOperations;
import io.riddles.go.game.board.GoBoard;
import io.riddles.go.game.move.GoMove;
import io.riddles.go.game.state.GoPlayerState;
import io.riddles.go.game.state.GoState;
import io.riddles.javainterface.exception.InvalidInputException;
import io.riddles.go.game.board.Point;
import io.riddles.javainterface.exception.InvalidMoveException;


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
        }
    }

    private void transformPlaceMove(GoState state, GoMove move, int playerId) {
        GoBoard board = state.getBoard();
        Point point = move.getCoordinate();

        String[][] originalBoard = getBoardArray(board);

        if (point.getX() > board.getWidth() || point.getY() > board.getHeight() || point.getX() < 0 || point.getY() < 0) { /* Move within range */
            move.setException(new InvalidMoveException("Move out of bounds"));
        }

        if (!board.getFieldAt(point).equals(GoBoard.EMPTY_FIELD)) { /*Field is not available */
            move.setException(new InvalidMoveException("Chosen position is already filled"));
        }

        board.setFieldAt(point, String.valueOf(playerId));
        board.setLastPosition(point);

        int stonesTaken = checkCaptures(board, playerId);
        move.setStonesTaken(stonesTaken);

        if (!checkSuicideRule(board, point, String.valueOf(playerId))) { /* Check Suicide Rule */
            move.setException(new InvalidMoveException("Illegal Suicide Move"));
        }

        if (move.getException() != null) {
            board.initializeFromArray(originalBoard);
        }
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

    public boolean isBoardFull(GoBoard board) {
        for(int y = 0; y < board.getHeight(); y++)
            for(int x = 0; x < board.getWidth(); x++)
                for (int playerId = 1; playerId <= 2; playerId++)
                    if (board.getFieldAt(new Point(x,y)).equals(GoBoard.EMPTY_FIELD) &&
                            checkSuicideRule(board, new Point(x,y), String.valueOf(playerId)))
                        return false;
        // No move can be played
        return true;
    }

    // Returns player score according to Tromp-Taylor Rules
    public int calculateScore(GoBoard board, int playerId) {
        int score = this.getPlayerStones(board, playerId);

        if (score <= 0) return 0;

        if (this.getPlayerStones(board, 2 - (playerId + 1)) == 0) { // opponent stones == 0
            if (score <= 1) {
                return score;
            }

            return board.getWidth() * board.getHeight();
        }

        /* Add empty points that reach only playerId color */
        boolean[][] mark = new boolean[board.getHeight()][board.getWidth()];
        mIsTerritory = false;
        mNrAffectedFields = 0;
        for(int y = 0; y < board.getHeight(); y++) {
            for(int x = 0; x < board.getWidth(); x++) {
                mCheckedFields[x][y] = false;
            }
        }

        for(int y = 0; y < board.getHeight(); y++) {
            for(int x = 0; x < board.getWidth(); x++) {
                Point point = new Point(x, y);
                if (board.getFieldAt(point).equals(GoBoard.EMPTY_FIELD) && !mCheckedFields[x][y]) {
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

    public int getPlayerStones(GoBoard board, int value) {
        int stones = 0;
        for(int y = 0; y < board.getHeight(); y++) {
            for (int x = 0; x < board.getWidth(); x++) {
                if (board.getFieldAt(new Point(x, y)).equals(value + "")) {
                    stones++;
                }
            }
        }
        return stones;
    }

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
            if (board.getFieldAt(p).equals(GoBoard.EMPTY_FIELD)) {
                mFoundLiberties++;
            }
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
                if (BoardOperations.compareFields(originalBoard, compareBoard) &&
                        !BoardOperations.compareFields(originalBoard, middleBoard)) {
                    return true;
                }
            }
        }
        return false;
    }
}