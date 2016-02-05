package com.theaigames.tictactoe.moves;

import com.theaigames.tictactoe.field.Field;
import com.theaigames.tictactoe.player.Player;

public class MoveResult {
	private String mOldFieldPresentationString, mNewFieldPresentationString;
	private int mMoveNumber = 0;
	private Player mPlayer;
	private Move mMove;

	public MoveResult(Player player, Move move, String oldFieldPresentationString, Field newField) {
	    mPlayer = player;
	    mMove = move;
	    mOldFieldPresentationString = oldFieldPresentationString;
	    mNewFieldPresentationString = newField.toPresentationString(mPlayer.getId(), false);
	}
	
	public String getOldFieldPresentationString() {
	    return mOldFieldPresentationString;
	}
	
	public String getNewFieldPresentationString() {
        return mNewFieldPresentationString;
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
}
