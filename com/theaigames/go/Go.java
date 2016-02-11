package com.theaigames.go;

import java.util.ArrayList;
import java.util.List;

import com.theaigames.engine.io.IOPlayer;
import com.theaigames.game.AbstractGame;
import com.theaigames.game.player.AbstractPlayer;
import com.theaigames.go.field.Field;
import com.theaigames.go.player.Player;

public class Go extends AbstractGame {
	
	private final int TIMEBANK_MAX = 10000;
	private final int TIME_PER_MOVE = 500;
	private List<Player> players;
	private Field mField;

	@Override
	public void setupGame(ArrayList<IOPlayer> ioPlayers) throws Exception {			
		// create all the players and everything they need
		this.players = new ArrayList<Player>();
		
		// create the playing field
		this.mField = new Field();
		
		for(int i=0; i<ioPlayers.size(); i++) {
			// create the player
			String playerName = String.format("player%d", i+1);
			Player player = new Player(playerName, ioPlayers.get(i), TIMEBANK_MAX, TIME_PER_MOVE, i+1);
			this.players.add(player);

		}
		for(Player player : this.players) {
			sendSettings(player);
		}
		
		// create the processor
		super.processor = new Processor(this.players, this.mField);
	}

	public void sendSettings(Player player) {
		String playerString = this.players.get(0).getName() + "," + this.players.get(1).getName();
		player.sendSetting("timebank", TIMEBANK_MAX);
		player.sendSetting("time_per_move", TIME_PER_MOVE);
		player.sendSetting("player_names", playerString);
		player.sendSetting("your_bot", player.getName());
		player.sendSetting("your_botid", player.getId());
	}

	@Override
	protected void runEngine() throws Exception {
	    System.out.println("starting...");
	    
		super.engine.setLogic(this);
		super.engine.start();
	}
	
	public static void main(String args[]) throws Exception {
		Go game = new Go();
		game.TEST_BOT = "java -cp /home/joost/workspace/GoBot/bin/ bot.BotStarter";
		game.NUM_TEST_BOTS = 2;
		game.setupEngine(args);
		game.runEngine();
	}

	@Override
	public void sendSettings(AbstractPlayer player) {
		// TODO Auto-generated method stub
		
	}
}
