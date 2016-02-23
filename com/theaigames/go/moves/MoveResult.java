package com.theaigames.go.moves;

import com.theaigames.go.field.Field;
import com.theaigames.go.player.Player;

public class MoveResult {
	private int mMoveNumber = 0;
	private Player mPlayer;
	private Move mMove;
	private Field mField;
	private String mString;
	public int mStonesPlayer1 = 0, mStonesPlayer2 = 0;
	public int mStonesTakenPlayer1 = 0, mStonesTakenPlayer2 = 0;
	public int mScorePlayer1 = 0, mScorePlayer2 = 0;

	public MoveResult(Player player, Move move, Field field, int stonesPlayer1, int stonesPlayer2, int stonesTakenPlayer1, int stonesTakenPlayer2) {
		mPlayer = player;
		mMove = move;
		mField = field;
		mStonesPlayer1 = stonesPlayer1;
		mStonesPlayer2 = stonesPlayer2;	
		mStonesTakenPlayer1 = stonesTakenPlayer1;
		mStonesTakenPlayer2 = stonesTakenPlayer2;		
		mScorePlayer1 = field.calculateScore(1);
		mScorePlayer2 = field.calculateScore(2);
		mString = field.toString();
	}
	
	public Player getPlayer() {
		return mPlayer;
	}
	
	public Move getMove() {
	    return mMove;
	}
	
	public void setMoveNumber(int moveNumber) {
		mMoveNumber = moveNumber;
	}
	
	public int getMoveNumber() {
		return mMoveNumber;
	}
	
	public String getAction() {
		return mMove.getAction();
	}
	
	public String toString() {
	    return mString;
	}
}
