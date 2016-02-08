// Copyright 2015 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//	
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package com.theaigames.go;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.theaigames.game.player.AbstractPlayer;
import com.theaigames.game.AbstractGame;
import com.theaigames.game.GameHandler;
import com.theaigames.go.field.Field;
import com.theaigames.go.moves.Move;
import com.theaigames.go.moves.MoveResult;
import com.theaigames.go.player.Player;

public class Processor implements GameHandler {
	
	private int mMoveNumber = 1;
	private int mRoundNumber = -1;
	private List<Player> mPlayers;
	private List<Move> mMoves;
	private List<MoveResult> mMoveResults;
	private Field mField;
	private int mGameOverByPlayerErrorPlayerId = 0;
	private int mStonesPlayer1, mStonesPlayer2;

	public Processor(List<Player> players, Field field) {
		mPlayers = players;
		mField = field;
		mMoves = new ArrayList<Move>();
		mMoveResults = new ArrayList<MoveResult>();
		if (AbstractGame.DEV_MODE) {
			System.out.println("Running in DEV_MODE");
			dbgTestKoRule();
			dbgTestCapture();
			
		}
		
	}
	
	private void dbgTestKoRule() {
		mField.addMove(2, 0, 1);
		recordMove(mPlayers.get(0), mField.toString());
		mField.addMove(1, 1, 1);
		recordMove(mPlayers.get(0), mField.toString());
		mField.addMove(2, 2, 1);
		recordMove(mPlayers.get(0), mField.toString());
		
		mField.addMove(3, 0, 2);
		recordMove(mPlayers.get(1), mField.toString());
		mField.addMove(2, 1, 2);
		recordMove(mPlayers.get(1), mField.toString());
		mField.addMove(3, 2, 2);
		recordMove(mPlayers.get(1), mField.toString());
		mField.addMove(4, 1, 2);
		recordMove(mPlayers.get(1), mField.toString());
		mField.addMove(3, 1, 1);
		recordMove(mPlayers.get(0), mField.toString());
		mField.addMove(2, 1, 2);
		recordMove(mPlayers.get(1), mField.toString());
	}

	
	private void dbgTestCapture() {
		
		mField.addMove(18, 1, 2);
		recordMove(mPlayers.get(1), mField.toString());
		mField.addMove(18, 2, 2);
		recordMove(mPlayers.get(1), mField.toString());
		
		mField.addMove(18, 0, 1);
		recordMove(mPlayers.get(0), mField.toString());
		mField.addMove(17, 1, 1);
		recordMove(mPlayers.get(0), mField.toString());
		mField.addMove(17, 2, 1);
		recordMove(mPlayers.get(0), mField.toString());
		mField.addMove(18, 3, 1);
		recordMove(mPlayers.get(0), mField.toString());
		//System.exit(0);

	}
	
	@Override
	public void playRound(int roundNumber) {
	    System.out.println(String.format("playing round %d", roundNumber));
	    mRoundNumber = roundNumber;
		for (Player player : mPlayers) {
			if (!isGameOver()) {
				player.sendUpdate("round", roundNumber);
				player.sendUpdate("move", mMoveNumber);
				player.sendUpdate("field", mField.toString());
				String response = player.requestMove("move");
				if (!parseResponse(response, player)) {
					response = player.requestMove("move");
					if (!parseResponse(response, player)) {
					    mGameOverByPlayerErrorPlayerId = player.getId(); /* Too many errors, other player wins */
					}
				}
				mMoveNumber++;
				if (AbstractGame.DEV_MODE) {
					//mField.dumpBoard();
				}
			}
		}
	}
	
	/**
	 * Parses player response and inserts disc in field
	 * @param args : command line arguments passed on running of application
	 * @return : true if valid move, otherwise false
	 */
	private Boolean parseResponse(String r, Player player) {
		String[] parts = r.split(" ");
		String s = mField.toString();
		if (parts[0].equals("place_move")) {
		    try {
    			int column = (int) Double.parseDouble(parts[1]);
    			int row = (int) Double.parseDouble(parts[2]);
    			
    			if (mField.addMove(column, row, player.getId())) {
                    recordMove(player, s);
                    return true;
                } else {
                    player.getBot().outputEngineWarning(mField.getLastError());
                }
		    } catch (Exception e) {
		        createParseError(player, r);
		    }
		} else {
		    createParseError(player, r);
		}
		recordMove(player, s);
		return false;
	}
	
	private void createParseError(Player player, String input) {
	    mField.setLastError("Error: failed to parse input");
        player.getBot().outputEngineWarning(String.format("Failed to parse input '%s'", input));
	}
	
	private void recordMove(Player player, String oldFieldPresentationString) {
		Move move = new Move(player);
		move.setMove(mField.getLastX(), mField.getLastY());
		move.setIllegalMove(mField.getLastError());
		System.out.println(mField.getLastError());
		mMoves.add(move);
		
		if (player.getId()==1) {
			mStonesPlayer1+= mField.getStonesTaken();
		} else {
			mStonesPlayer2+= mField.getStonesTaken();
		}
		MoveResult moveResult = new MoveResult(player, move, mField, mStonesPlayer1, mStonesPlayer2);
		moveResult.setMoveNumber(mMoveNumber);

		mMoveResults.add(moveResult);
	}
	
	@Override
	public int getRoundNumber() {
		return this.mRoundNumber;
	}

	@Override
	public AbstractPlayer getWinner() {
		int winner = mField.getWinner();
		if (mGameOverByPlayerErrorPlayerId > 0) { /* Game over due to too many player errors. Look up the other player, which became the winner */
			for (Player player : mPlayers) {
				if (player.getId() != mGameOverByPlayerErrorPlayerId) {
					return player;
				}
			}
		}
		if (winner != 0) {
			for (Player player : mPlayers) {
				if (player.getId() == winner) {
					return player;
				}
			}
		}
		return null;
	}

	@Override
	public String getPlayedGame() {
		JSONObject output = new JSONObject();
		AbstractPlayer winner = getWinner();
		
		String sWinner = "";
		if (winner == null) {
			sWinner = "none";
		} else {
			sWinner = winner.getName();
		}
		try {
			JSONArray playerNames = new JSONArray();
			for(Player player : this.mPlayers) {
				playerNames.put(player.getName());
			}

			output.put("settings", new JSONObject()
			.put("field", new JSONObject()
					.put("width", String.valueOf(getField().getNrColumns()))
					.put("height", String.valueOf(getField().getNrRows())))
			.put("players", new JSONObject()
					.put("count", this.mPlayers.size())
					.put("names", playerNames))
					.put("winnerplayer", sWinner)
			);

			JSONArray states = new JSONArray();
			int counter = 0;
			
			for (MoveResult move : mMoveResults) {

				JSONObject state = new JSONObject();
				state.put("field", move.toString());
				state.put("move", move.getMoveNumber());
				state.put("winner", "");
				state.put("player", move.getPlayer().getId());
				state.put("player1stonestaken", move.mStonesPlayer1);
				state.put("player2stonestaken", move.mStonesPlayer2);
                state.put("illegalMove", move.getMove().getIllegalMove());
				states.put(state);
				
				if (counter == mMoveResults.size()-1) { // final overlay state with winner
				    String winnerstring = "";
                    if (winner == null) {
                        winnerstring = "none";
                    } else {
                        winnerstring = winner.getName();
                    }
                    JSONObject state3 = new JSONObject();
                    state3.put("field", move.toString());
                    state3.put("move", move.getMoveNumber());
                    state3.put("winner", winnerstring);
                    state3.put("player", move.getPlayer().getId());
                    state3.put("illegalMove", move.getMove().getIllegalMove());
                    states.put(state3);
                }
				counter++;
			}
			output.put("states", states);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}


	/**
	 * Returns a List of Moves played in this game
	 * @param args : 
	 * @return : List with Move objects
	 */
	public List<Move> getMoves() {
		return mMoves;
	}

	public Field getField() {
		return mField;
	}

	@Override
	public boolean isGameOver() {
		return (!mField.isMoveAvailable() || getWinner() != null);
	}
}