package com.theaigames.go.moves;

import com.theaigames.go.field.Field;
import com.theaigames.go.player.Player;

public class MoveResult {
	private int mMoveNumber = 0;
	private Player mPlayer;
	private Move mMove;
	private Field mField;
	private String mString;
	public int mStonesPlayer1 = 0;
	public int mStonesPlayer2 = 0;

	public MoveResult(Player player, Move move, Field field, int stonesPlayer1, int stonesPlayer2) {
		mPlayer = player;
		mMove = move;
		mField = field;
		mStonesPlayer1 = stonesPlayer1;
		mStonesPlayer2 = stonesPlayer2;
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
