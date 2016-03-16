package com.theaigames.go.field;

import java.util.List;

import com.theaigames.go.player.Player;
import com.theaigames.util.Util;

public class Field {
	private int[][] mBoard;
	private int[][][] mPreviousBoards; /* For checking Ko rule */
	private int mFoundLiberties, mNrAffectedFields; /* For checking groups */
	private Boolean[][] mAffectedFields; /* For checking groups */
	private Boolean[][] mCheckedFields; /* For checking groups */

	private int[] mTotalStonesTaken;
	private int[] mPlayerScores;
	private Boolean mIsTerritory = false;
	
	private int mCols = 0, mRows = 0;
	private String mLastError = "";
	private int mLastX = -1, mLastY = -1;
	
	public Field(int width, int height, List<Player> players) {
		mCols = width;
		mRows = height;
		mBoard = new int[mCols][mRows];
		mPreviousBoards = new int[3][mCols][mRows];
		mAffectedFields = new Boolean[mCols][mRows];
		mCheckedFields = new Boolean[mCols][mRows];
		
		mTotalStonesTaken = new int[players.size() + 1];
		mPlayerScores = new int[players.size() + 1];
		for (Player player : players) {
		    mTotalStonesTaken[player.getId()] = 0;
		    mPlayerScores[player.getId()] = 0;
		}
		
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
		int[][] originalBoard = new int[mBoard.length][mBoard[0].length];
		for(int i=0; i<mBoard.length; i++)
			  for(int j=0; j<mBoard[0].length; j++)
				  originalBoard[i][j]=mBoard[i][j];
		mLastError = "";
		/* Check legality of move */
		if (x < 0 || x >= mCols || y >= mRows || y < 0) { /* Move out of bounds */
			mLastError = "Error: move out of bounds";
			return false;
		}
		if (mBoard[x][y] != 0) { /*Field is not available */
			mLastError = "Error: chosen position is already filled";
			return false;
		}
		/* Field is available */
		mBoard[x][y] = move;
		mLastX = x;
		mLastY = y;

		int stonesTaken = checkCaptures(move);
		if (!checkSuicideRule(x, y, move)) { /* Check Suicide Rule */
			mLastError = "Error: illegal Suicide Move";
			/* Undo move */
			for(int i=0; i<mBoard.length; i++)
				  for(int j=0; j<mBoard[0].length; j++)
					  mBoard[i][j]=originalBoard[i][j];
			recordHistory();
			return false;
		}
		if (!checkKoRule(x, y)) { /* Check Ko Rule */
			mLastError = "Error: violation of Ko Rule";
			/* Undo move */
			for(int i=0; i<mBoard.length; i++)
				  for(int j=0; j<mBoard[0].length; j++)
					  mBoard[i][j]=originalBoard[i][j];
			recordHistory();
			return false;
		}
		updateTotalStonesTaken(move, stonesTaken);
		updatePlayerScores();
		recordHistory();
		return true;
	}
	
	/**
	 * Checks the Ko Rule. (A move that returns the game to the previous position)
	 * @param args : int x, int y, int move
	 * @return : true if legal move otherwise false
	 */
	private Boolean checkKoRule(int x, int y) {
		/* If board is the same as 2 moves back, it is an illegal move. */
		if (Util.compareBoards(mBoard, mPreviousBoards[1])) {
			return false;
		}
		return true;
	}
	
	/**
	 * Checks the Suicide Rule. (A move which creates a group that immediately has no liberties)
	 * @param args : int x, int y, int move
	 * @return : true if legal move otherwise false
	 */
	private Boolean checkSuicideRule(int x, int y, int move) {
		mFoundLiberties = 0;
		Boolean[][] mark = new Boolean[mRows][mCols];
		for (int tx = 0; tx < mRows; tx++) {
			for (int ty = 0; ty < mCols; ty++) {
				mAffectedFields[tx][ty] = false;
				mark[tx][ty] = false;
			}
		}
		flood(mark, x, y, move, 0);
		return (mFoundLiberties > 0);
	}
	
	/**
	 * Keeps record of the last two boards, to check the Ko Rule
	 * @param args : 
	 * @return : 
	 */
	private void recordHistory() {
		for (int i = 0; i < mPreviousBoards.length-1; i++) {			
			for (int x = 0; x < mRows; x++) {
				for (int y = 0; y < mCols; y++) {
					mPreviousBoards[i][x][y] = mPreviousBoards[i+1][x][y];
				}
			}
		}
		for (int x = 0; x < mRows; x++) {
			for (int y = 0; y < mCols; y++) {
				mPreviousBoards[mPreviousBoards.length-1][x][y] = mBoard[x][y];
			}
		}
	}

	/**
	 * Check for captures stones or stone groups
	 * @param args : 
	 * @return : 
	 */
	private int checkCaptures(int move) {
		int stonesTaken = 0;
		for (int x = 0; x < mRows; x++) {
			for (int y = 0; y < mCols; y++) {
				if (mBoard[x][y] > 0 && mBoard[x][y] != move) {
					mFoundLiberties = 0;
					Boolean[][] mark = new Boolean[mRows][mCols];
					for (int tx = 0; tx < mRows; tx++) {
						for (int ty = 0; ty < mCols; ty++) {
							mAffectedFields[tx][ty] = false;
							mark[tx][ty] = false;
						}
					}
					flood(mark, x, y, mBoard[x][y], 0);
					if (mFoundLiberties == 0) { /* Group starves */
//						System.out.println("STARVE " + x + " " + y);
						for (int tx = 0; tx < mRows; tx++) {
							for (int ty = 0; ty < mCols; ty++) {
								if (mAffectedFields[tx][ty]) {
									mBoard[tx][ty] = 0;
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
	 * Recursive function to check stone group liberties
	 * @param args : 
	 * @return : 
	 */
	private void flood(Boolean [][]mark, int x, int y, int srcColor, int stackCounter) {
		// Make sure row and col are inside the board
		if (x < 0) return;
		if (y < 0) return;
		if (x >= mRows) return;
		if (y >= mCols) return;
		
		// Make sure this field hasn't been visited yet
		if (mark[x][y]) return;
		
		// Make sure this field is the right color to fill
		if (mBoard[x][y] != srcColor) {
			if (mBoard[x][y] == 0) { mFoundLiberties++; }
			return;
		}
		
		// Fill field with target color and mark it as visited
		mAffectedFields[x][y] = true;
		mark[x][y] = true;
		
		// Recursively fill surrounding fields
		if (stackCounter < 512) {
			flood(mark, x - 1, y, srcColor, stackCounter+1);
			flood(mark, x + 1, y, srcColor, stackCounter+1);
			flood(mark, x, y - 1, srcColor, stackCounter+1);
			flood(mark, x, y + 1, srcColor, stackCounter+1);
		}
	}
	
//	private Boolean fieldHasLiberties(int x, int y) {
//		Boolean libertyLeft = true, libertyRight = true, libertyUp = true, libertyDown = true;
//		if (x == 0) {
//			libertyLeft = false;
//		} else {
//			if (mBoard[x-1][y] != 0) 
//				libertyLeft = false;
//		}
//		if (x == mCols-1) {
//			libertyRight = false;
//		} else {
//			if (mBoard[x+1][y] != 0) 
//				libertyRight = false;
//		}
//		if (y == 0) {
//			libertyUp = false;
//		} else {
//			if (mBoard[x][y-1] != 0) 
//				libertyUp = false;
//		}
//		if (y == mRows-1) {
//			libertyDown = false;
//		} else {
//			if (mBoard[x][y+1] != 0) 
//				libertyDown = false;
//		}
//		return (libertyLeft || libertyRight || libertyUp || libertyDown);
//	}
	
	
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
	 * Checks whether there is any move available on the field
	 * @param args : 
	 * @return : Returns true when there is no move available, otherwise returns false.
	 */
	public boolean boardIsFull() {
		for (int x = 0; x < mCols; x++)
			for (int y = 0; y < mRows; y++)
				for (int playerId = 1; playerId <= 2; playerId++)
				    if (mBoard[x][y] == 0 && checkSuicideRule(x, y, playerId))
				        return false;
		// No move can be played
		return true;
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
	
	/**
	 * Returns player score according to Tromp-Taylor Rules
	 * @param args : int playerId
	 * @return : int player score
	 */
	public int calculateScore(int playerId) {
		int score = this.getPlayerStones(playerId);
		if (score <= 0) return 0;
		
		/* Add empty points that reach only playerId color */
		Boolean[][] mark = new Boolean[mRows][mCols];		
		for (int x = 0; x < mRows; x++) {
			for (int y = 0; y < mCols; y++) {
				mCheckedFields[x][y] = false;
			}
		}

		mNrAffectedFields = 0;
		for (int y = 0; y < mRows; y++) {
			for (int x = 0; x < mCols; x++) {
				if (mBoard[x][y] == 0 && mCheckedFields[x][y] == false) {
					for (int tx = 0; tx < mRows; tx++) {
						for (int ty = 0; ty < mCols; ty++) {
							mAffectedFields[tx][ty] = false;
							mark[tx][ty] = false;

						}
					}
					
					mIsTerritory = true;
					mNrAffectedFields = 0;
					floodFindTerritory(mark, x, y, playerId, 0);

					if (mIsTerritory) {
						score += mNrAffectedFields;
						for (int tx = 0; tx < mRows; tx++) {
							for (int ty = 0; ty < mCols; ty++) {
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
	 * Recursive function to check stone group liberties.
	 * Sets mIsTerritory member variable with result.
	 * @param args : 
	 * @return : 
	 */
	private void floodFindTerritory(Boolean [][]mark, int x, int y, int playerid, int stackCounter) {
		/* Strategy: 
		 * If edge other than (playerid or 0 or board edge) has been found, then no territory.
		 */
		// Make sure row and col are inside the board
		if (x < 0) { return; }
		if (y < 0) { return; }
		if (x >= mRows) { return; }
		if (y >= mCols) { return; }

		// Make sure this field hasn't been visited yet
		if (mark[x][y]) return;
		
		// Make sure this field is the right color to fill
		if (mBoard[x][y] > 0) {
			if (mBoard[x][y] != playerid) {
				mIsTerritory = false;
			}
			return;
		}	
		
		mAffectedFields[x][y] = true;
		
		// Mark field as visited
		mNrAffectedFields++;
		mark[x][y] = true;

		// Recursively check surrounding fields
		if (stackCounter < 512) {
			floodFindTerritory(mark, x - 1, y, playerid, stackCounter+1);
			floodFindTerritory(mark, x + 1, y, playerid, stackCounter+1);
			floodFindTerritory(mark, x, y - 1, playerid, stackCounter+1);
			floodFindTerritory(mark, x, y + 1, playerid, stackCounter+1);
		}
	}
	
	/**
	 * Dumps affectedFields to stdout
	 * @param args : 
	 * @return : 
	 */
	public void dumpAffectedFields() {
		System.out.print("\n\n");
		for (int y = 0; y < mRows; y++) {
			for (int x = 0; x < mCols; x++) {
				String a = (mAffectedFields[x][y]) ? "1" : "0";
				System.out.print(a);
				if (x < mCols-1) {
					String s = ", ";
					System.out.print(s);
				}
			}
			System.out.print("\n");
		}
	}
	
	/**
	 * Gets the amount of stones the given player has on the board
	 * @param playerId : player id
	 * @return : amount of stones on the board
	 */
	public int getPlayerStones(int playerId) {
	    int stones = 0;
	    for (int y = 0; y < mRows; y++) {
            for (int x = 0; x < mCols; x++) {
                if (mBoard[x][y] == playerId) {
                    stones++;
                }
            }
        }
	    return stones;
	}
	
	public void updateTotalStonesTaken(int playerId, int stonesTaken) {
	    mTotalStonesTaken[playerId] += stonesTaken;
	}
	
	public int getTotalStonesTaken(int playerId) {
	    return mTotalStonesTaken[playerId];
	}
	
	public void updatePlayerScores() {
	    for (int id = 1; id <= 2; id++) {
	        mPlayerScores[id] = calculateScore(id);
	    }
	}
	
	public int getPlayerScore(int playerId) {
	    return mPlayerScores[playerId];
	}
}

