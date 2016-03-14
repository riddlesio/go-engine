package com.theaigames.go.player;

import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.player.AbstractPlayer;

public class Player extends AbstractPlayer {
	int mId;
	public Player(String name, IOPlayer bot, long maxTimeBank, long timePerMove, int id) {
		super(name, bot, maxTimeBank, timePerMove);
		mId = id;
	}

	public int getId() {
		return mId;
	}
}
