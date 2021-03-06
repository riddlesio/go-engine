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

package io.riddles.go.game.board;


/**
 * io.riddles.go.game.board.Board - Created on 11-7-16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class GoBoard {

    protected String[][] field;
    protected int width = 19;
    protected int height = 19;
    public static final String EMPTY_FIELD = ".";

    protected boolean[][] p1SuicidePoints;
    protected boolean[][] p2SuicidePoints;

    private int mLastX = -1, mLastY = -1;


    public GoBoard(int width, int height) {
        this.width = width;
        this.height = height;
        field = new String[width][height];
        p1SuicidePoints = new boolean[width][height];
        p2SuicidePoints = new boolean[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                p1SuicidePoints[x][y] = true;
                p2SuicidePoints[x][y] = true;
            }
        }
        clearBoard();
    }

    public GoBoard clone() {
        GoBoard cBoard = new GoBoard(this.width, this.height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point p = new Point(x, y);
                cBoard.setFieldAt(p, getFieldAt(p));
                cBoard.setSuicidePoint(1, p, p1SuicidePoints[x][y]);
                cBoard.setSuicidePoint(2, p, p2SuicidePoints[x][y]);
            }
        }
        return cBoard;
    }

    public void clearBoard() {
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                field[x][y] = EMPTY_FIELD;
            }
        }
    }


    public void setSuicidePoint(int player, Point p, boolean set) {
        if (player == 1)
            p1SuicidePoints[p.getX()][p.getY()] = set;
        else
            p2SuicidePoints[p.getX()][p.getY()] = set;
    }

    public void dumpBoard() {
        System.out.print("\n\n");
        for(int y = 0; y < height; y++) {
            for(int x = 0; x < width; x++) {
                System.out.print(field[x][y]);
                if (x < width-1) {
                    String s = ", ";
                    if (x == mLastX && y == mLastY) {
                        s = "* ";
                    }
                    System.out.print(s);
                }
            }
            System.out.print("\n");
        }
    }

    @Override
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (counter > 0) r += ",";
                r += field[x][y];
                counter++;
            }
        }
        return r;
    }

    /**
     * Returns board in String format, takes playerId to add suicide fields.
     * @param playerId : player id
     * @return : String
     */
    public String toString(int playerId) {
        String r = "";
        int counter = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (counter > 0) r += ",";
                if (playerId == 0 && !p1SuicidePoints[x][y]) r += "-1";
                else if (playerId == 1 && !p2SuicidePoints[x][y]) r += "-1";
                else r += field[x][y];
                counter++;
            }
        }
        return r;
    }

    /**
     * Gets the amount of stones the given player has on the board
     * @param playerId : player id
     * @return : amount of stones on the board
     */
    public int getPlayerStones(int playerId) {
        int stones = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (field[x][y].equals(String.valueOf(playerId))) {
                    stones++;
                }
            }
        }
        return stones;
    }

    public String getFieldAt(Point c) {
        return field[c.x][c.y];
    }

    public void setFieldAt(Point c, String f) {
        field[c.x][c.y] = f;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void initializeFromArray(String[][] array) {
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                field[x][y] = array[x][y];
            }
        }
    }

    public void setLastPosition(Point c) {
        mLastX = c.x;
        mLastY = c.y;
    }
}
