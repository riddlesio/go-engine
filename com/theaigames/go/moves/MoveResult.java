package com.theaigames.go.moves;

import com.theaigames.go.field.Field;
import com.theaigames.go.player.Player;

public class MoveResult {
	private int mMoveNumber = 0;
	private Player mPlayer;
	private Move mMove;
	private Field mField;
	private String mString;

	public MoveResult(Player player, Move move, Field field) {
		mPlayer = player;
		mMove = move;
		mField = field;
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
	
	public String toString() {
	    return mString;
	}
}
