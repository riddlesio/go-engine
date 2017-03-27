package io.riddles.go.game.board;


/**
 * io.riddles.go.game.board.Board - Created on 11-7-16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class GoBoard {

    private final int KO_SEARCH_DEPTH = 30;


    protected String[][] field;
    protected int width = 19;
    protected int height = 19;
    public static final String EMPTY_FIELD = ".";


    private String mLastError = "";
    private int mLastX = -1, mLastY = -1;


    public GoBoard(int width, int height) {
        this.width = width;
        this.height = height;
        field = new String[width][height];

        clearBoard();
    }

    public GoBoard clone() {
        GoBoard cBoard = new GoBoard(this.width, this.height);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Point p = new Point(x, y);
                cBoard.setFieldAt(p, getFieldAt(p));
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

    /**
     * Dumps the board to stdout
     * @param args :
     * @return :
     */
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
    /**
     * Creates comma separated String with player ids.
     * @param args :
     * @return : String with player ids for every cell, or 0 when cell is empty.
     */
    public String toString() {
        String r = "";
        int counter = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (counter > 0) {
                    r += ",";
                }
                r += field[x][y];
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

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }


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
