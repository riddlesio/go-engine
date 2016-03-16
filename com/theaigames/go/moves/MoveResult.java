package com.theaigames.go.moves;

import com.theaigames.go.field.Field;
import com.theaigames.go.player.Player;

public class MoveResult {
	private int mRoundNumber;
	private Player mPlayer;
	private Move mMove;
	private String mString;
	private int totalStones[] = new int[2];
	private int stonesTaken[] = new int[2];
	private int totalScore[] = new int[2];

	public MoveResult(Player player, Player opponent, Move move, int roundNumber, Field field) {
	    int pId = player.getId();
	    int oId = opponent.getId();
	    
		mPlayer = player;
		mMove = move;
		mRoundNumber = roundNumber;
		totalStones[pId] = field.getPlayerStones(pId);
		totalStones[oId] = field.getPlayerStones(pId);
		stonesTaken[pId] = field.getTotalStonesTaken(pId);
		stonesTaken[oId] = field.getTotalStonesTaken(oId);
		totalScore[pId] = field.getPlayerScore(pId);
		totalScore[oId] = field.getPlayerScore(oId);
		mString = field.toString();
	}
	
	public Player getPlayer() {
		return mPlayer;
	}
	
	public Move getMove() {
	    return mMove;
	}
	
	public int getRoundNumber() {
		return mRoundNumber;
	}
	
	public String getFieldString() {
	    return mString;
	}
	
	public int getTotalStones(int playerId) {
	    return totalStones[playerId];
	}
	
	public int getStonesTaken(int playerId) {
	    return stonesTaken[playerId];
	}
	
	public int getTotalScore(int playerId) {
	    return totalScore[playerId];
	}
}
