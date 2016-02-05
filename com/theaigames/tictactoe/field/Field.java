package com.theaigames.tictactoe.field;

import com.theaigames.util.Util;

public class Field {
	private int[][] mBoard;
	private int[][] mMacroboard;

	private int mCols = 0, mRows = 0;
	private String mLastError = "";
	private int mLastX = -1, mLastY = -1;
	private Boolean mAllMicroboardsActive = true;
	
	public Field() {
		mCols = 9;
		mRows = 9;
		mBoard = new int[mCols][mRows];
		mMacroboard = new int[mCols / 3][mRows / 3];
		clearBoard();
	}
	
	public void clearBoard() {
		for (int x = 0; x < mCols; x++) {
			for (int y = 0; y < mRows; y++) {
				mBoard[x][y] = 0;
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
		for (int y = 0; y < mRows; y++) {
			for (int x = 0; x < mCols; x++) {
				System.out.print(mBoard[x][y]);
				if (x < mCols-1) {
					String s = ", ";
					if (x % 3 == 2) {
						s = "| ";
					}
					if (x == mLastX && y == mLastY) {
						s = "* ";
					}
					System.out.print(s);
				}
			}
			if (y % 3 == 2) {
				System.out.print("\n");
				for (int x = 0; x < mCols-1; x++) {
					System.out.print("---");
				}
			}
			System.out.print("\n");
		}
		System.out.print("Macroboard:\n");
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {

				System.out.print(Util.padRight(mMacroboard[x][y] + "",3));
			}
			System.out.print("\n");
		}
	}

	/**
	 * Adds a move to the board
	 * @param args : int x, int y, int move
	 * @return : true if disc fits, otherwise false
	 */
	public Boolean addMove(int x, int y, int move) {
		mLastError = "";
		if (x < mCols && y < mRows && x >= 0 && y >= 0) { /* Move within range */
			if (isInActiveMicroboard(x, y)) { /* Move in active microboard */
				if (mBoard[x][y] == 0) { /*Field is available */
					mBoard[x][y] = move;
					mLastX = x;
					mLastY = y;
					updateMacroboard();
					return true;
				} else {
					mLastError = "Error: chosen position is already filled";
				}
			} else {
				mLastError = "Error: move not in active macroboard";
			}
		} else {
			mLastError = "Error: move out of bounds";
		}
		return false;
	}
	
	/**
	 * Returns whether microboard is full OR taken
	 * @param args : int x, int y
	 * @return : Boolean
	 */
	public Boolean microboardFull(int x, int y) {
		if (x < 0 || y < 0) return true; /* empty board */
		
		if (mMacroboard[x][y] == 1 || mMacroboard[x][y] == 2) { /* microboard is taken */
			return true;
		}
		for (int my = y*3; my < y*3+3; my++) {
			for (int mx = x*3; mx < x*3+3; mx++) {
				if (mBoard[mx][my] == 0)
					return false;
			}
		}
		return true; /* microboard is full */
	}
	
	/**
	 * Returns whether field is in active microboard
	 * @param args : int x, int y
	 * @return : Boolean
	 */
	public Boolean isInActiveMicroboard(int x, int y) {
		return mMacroboard[(int) Math.floor(x/3)][(int) Math.floor(y/3)] == -1;
	}
	
	/**
	 * Returns active microboard column
	 * @param args : 
	 * @return : active microboard column, or -1 if multiple microboards are active
	 */
	public int getActiveMicroboardX() {
		if (mAllMicroboardsActive) return -1;
		return mLastX%3;
	}
	
	/**
	 * Returns active microboard row
	 * @param args : 
	 * @return : active microboard row, or -1 if multiple microboards are active
	 */
	public int getActiveMicroboardY() {
		if (mAllMicroboardsActive) return -1;
		return mLastY%3;
	}
	
	/**
	 * Returns reason why addMove returns false
	 * @param args : 
	 * @return : reason why addMove returns false
	 */
	public String getLastError() {
		return mLastError;
	}
	
	public void setLastError(String error) {
	    mLastError = error;
	}
	
	/**
	 * Returns last inserted column
	 * @param args : 
	 * @return : last inserted column
	 */
	public int getLastX() {
		return mLastX;
	}
	
	/**
	 * Returns last inserted row
	 * @param args : 
	 * @return : last inserted row
	 */
	public int getLastY() {
		return mLastY;
	}
	
	@Override
	/**
	 * Creates comma separated String with player ids for the microboards.
	 * @param args : 
	 * @return : String with player names for every cell, or 'empty' when cell is empty.
	 */
	public String toString() {
		String r = "";
		int counter = 0;
		for (int y = 0; y < mRows; y++) {
			for (int x = 0; x < mCols; x++) {
				if (counter > 0) {
					r += ",";
				}
				r += mBoard[x][y];
				counter++;
			}
		}
		return r;
	}
	
	/**
	 * Creates a string with comma separated ints for every cell.
	 * @param args : 
	 * @return : String with comma separated ints for every cell.
	 * Format:		 LSB
	 * 0 0 0 0 0 0 0 0
	 * | | | | | | | |_ Player 1
	 * | | | | | | |___ Player 2
	 * | | | | | |_____ Active Player 1
	 * | | | | |_______ Active Player 2
	 * | | | |_________ Taken Player 1
	 * | | |___________ Taken Player 2
	 * | |_____________ Reserved
	 * |_______________ Reserved
	 */
	public String toPresentationString(int nextPlayer, Boolean showPossibleMoves) {
		String r = "";
		int counter = 0;
		for (int y = 0; y < mRows; y++) {
			for (int x = 0; x < mCols; x++) {
				int b = 0;
				if (mBoard[x][y] == 1) {
					b = b | (1 << 0);
				}
				if (mBoard[x][y] == 2) {
					b = b | (1 << 1);
				}
				if (showPossibleMoves) {
    				if (isInActiveMicroboard(x, y) && nextPlayer == 1 && mBoard[x][y] == 0) {
    					b = b | (1 << 2);
    				}
    				if (isInActiveMicroboard(x, y) && nextPlayer == 2 && mBoard[x][y] == 0) {
    					b = b | (1 << 3);
    				}
				}
				if (mMacroboard[x/3][y/3] == 1) {
					b = b | (1 << 4);
				}
				if (mMacroboard[x/3][y/3] == 2) {
					b = b | (1 << 5);
				}
				if (counter > 0) {
					r += ",";
				}
				r += b;
				counter++;
			}
		}
		return r;
	}
	
	/**
	 * Creates comma separated String with player ids for the macroboard.
	 * @param args : 
	 * @return : String with player ids for every cell, or 0 when cell is empty, or -1 when cell is ready for a move.
	 */
	public String macroboardToString() {
		String r = "";
		int counter = 0;
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 3; x++) {
				if (counter > 0) {
					r += ",";
				}
				r += mMacroboard[x][y];
				counter++;
			}
		}
		return r;
	}
	
	/**
	 * Checks whether the field is full
	 * @param args : 
	 * @return : Returns true when field is full, otherwise returns false.
	 */
	public boolean boardIsFull() {
		for (int x = 0; x < mCols; x++)
			for (int y = 0; y < mRows; y++)
				if (mBoard[x][y] == 0)
					return false; // At least one cell is not filled
		// All cells are filled
		return true;
	}
	
	/**
	 * Checks if there is a winner, if so, returns player id.
	 * @param args : 
	 * @return : Returns player id if there is a winner, otherwise returns 0.
	 */
	public int getWinner() {
		updateMacroboard();
		return checkMacroboardWinner();
	}
	
	/**
	 * Checks the microboards for wins and updates internal representation (macroboard)
	 * @param args : 
	 * @return : 
	 */
	public void updateMacroboard() {
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				int winner = getMicroboardWinner(x, y);
				mMacroboard[x][y] = winner;
			}
		}
		if (!microboardFull(mLastX%3, mLastY%3)) {
			mMacroboard[mLastX%3][mLastY%3] = -1;
		} else {
			for (int x = 0; x < 3; x++) {
				for (int y = 0; y < 3; y++) {
					if (!microboardFull(x, y)) {
						mMacroboard[x][y] = -1;
					}
				}
			}
		}
	}	
	
	/**
	 * Checks the microboard for a winner
	 * @param args : int x (0-2), int y (0-2)
	 * @return : player id of winner or 0
	 */
	public int getMicroboardWinner(int macroX, int macroY) {
		int startX = macroX*3;
		int startY = macroY*3;
		/* Check horizontal wins */
		for (int y = startY; y < startY+3; y++) {
			if (mBoard[startX+0][y] == mBoard[startX+1][y] && mBoard[startX+1][y] == mBoard[startX+2][y] && mBoard[startX+0][y] > 0) {
				return mBoard[startX+0][y];
			}
		}
		/* Check vertical wins */
		for (int x = startX; x < startX+3; x++) {
			if (mBoard[x][startY+0] == mBoard[x][startY+1] && mBoard[x][startY+1] == mBoard[x][startY+2] && mBoard[x][startY+0] > 0) {
				return mBoard[x][startY+0];
			}
		}
		/* Check diagonal wins */
		if (mBoard[startX][startY] == mBoard[startX+1][startY+1] && mBoard[startX+1][startY+1] == mBoard[startX+2][startY+2] && mBoard[startX+0][startY+0] > 0) {
			return mBoard[startX][startY];
		}
		if (mBoard[startX+2][startY] == mBoard[startX+1][startY+1] && mBoard[startX+1][startY+1] == mBoard[startX][startY+2] && mBoard[startX+2][startY+0] > 0) {
			return mBoard[startX+2][startY];
		}
		return 0;
	}
	
	/**
	 * Checks the macroboard for a winner
	 * @param args :
	 * @return : player id of winner or 0
	 */
	public int checkMacroboardWinner() {
		/* Check horizontal wins */
		for (int y = 0; y < 3; y++) {
			if (mMacroboard[0][y] == mMacroboard[1][y] && mMacroboard[1][y] == mMacroboard[2][y] && mMacroboard[0][y] > 0) {
				return mMacroboard[0][y];
			}
		}
		/* Check vertical wins */
		for (int x = 0; x < 3; x++) {
			if (mMacroboard[x][0] == mMacroboard[x][1] && mMacroboard[x][1] == mMacroboard[x][2] && mMacroboard[x][0] > 0) {
				return mMacroboard[x][0];
			}
		}
		/* Check diagonal wins */
		if (mMacroboard[0][0] == mMacroboard[1][1] && mMacroboard[1][1] == mMacroboard[2][2] && mMacroboard[0][0] > 0) {
			return mMacroboard[0][0];
		}
		if (mMacroboard[2][0] == mMacroboard[1][1] && mMacroboard[1][1] == mMacroboard[0][2] && mMacroboard[2][0] > 0) {
			return mMacroboard[2][0];
		}
		return 0;
	}

	public int getNrColumns() {
		return mCols;
	}
	
	public int getNrRows() {
		return mRows;
	}
	
	/**
	 * Checks the board for available moves, takes conquered microboard in account
	 * @param args : 
	 * @return : Boolean
	 */
	public Boolean isMoveAvailable() {
	    for (int y = 0; y < 3; y++) {
	        for (int x = 0; x < 3; x++) {
	        	if (!this.microboardFull(x, y)) return true;
	        }
	    }
	    return false;
	}
}
