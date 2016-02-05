package com.theaigames.go.field;

import com.theaigames.util.Util;

public class Field {
	private int[][] mBoard;

	private int mCols = 0, mRows = 0;
	private String mLastError = "";
	private int mLastX = -1, mLastY = -1;
	
	public Field() {
		mCols = 19;
		mRows = 19;
		mBoard = new int[mCols][mRows];
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
					if (x == mLastX && y == mLastY) {
						s = "* ";
					}
					System.out.print(s);
				}
			}
			System.out.print("\n");
		}
	}

	/**
	 * Adds a move to the board
	 * @param args : int x, int y, int move
	 * @return : true if legal move otherwise false
	 */
	public Boolean addMove(int x, int y, int move) {
		mLastError = "";
		if (x < mCols && y < mRows && x >= 0 && y >= 0) { /* Move within range */
			if (mBoard[x][y] == 0) { /*Field is available */
				mBoard[x][y] = move;
				mLastX = x;
				mLastY = y;
				return true;
			} else {
				mLastError = "Error: chosen position is already filled";
			}
		} else {
			mLastError = "Error: move out of bounds";
		}
		return false;
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
		// TODO: implement this
		return 0;
	}

	public int getNrColumns() {
		return mCols;
	}
	
	public int getNrRows() {
		return mRows;
	}
	
	/**
	 * Checks the board for available moves
	 * @param args : 
	 * @return : Boolean
	 */
	public Boolean isMoveAvailable() {
	    return !boardIsFull();
	}
}
