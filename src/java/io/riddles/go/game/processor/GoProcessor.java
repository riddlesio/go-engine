/*
 * Copyright 2016 riddles.io (developers@riddles.io)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     For the full copyright and license information, please view the LICENSE
 *     file that was distributed with this source code.
 */

package io.riddles.go.game.processor;

import java.util.ArrayList;

import io.riddles.go.game.state.GoPlayerState;
import io.riddles.go.game.move.*;
import io.riddles.go.game.player.GoPlayer;
import io.riddles.go.game.state.GoState;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.game.processor.PlayerResponseProcessor;
import io.riddles.javainterface.game.state.AbstractPlayerState;
import io.riddles.javainterface.io.PlayerResponse;

/**
 * io.riddles.go.game.processor.GoProcessor - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class GoProcessor extends PlayerResponseProcessor<GoState, GoPlayer> {

    private GoLogic logic;

    public GoProcessor(PlayerProvider<GoPlayer> playerProvider) {
        super(playerProvider);
        this.logic = new GoLogic();
    }

    @Override
    public GoState createNextStateFromResponse(GoState state, PlayerResponse input, int roundNumber) {

        /* Clone playerStates for next State */
        ArrayList<GoPlayerState> nextPlayerStates = clonePlayerStates(state.getPlayerStates());

        GoState nextState = new GoState(state, nextPlayerStates, roundNumber);
        nextState.setPlayerId(input.getPlayerId());

        GoPlayerState playerState = getActivePlayerState(nextPlayerStates, input.getPlayerId());
        playerState.setPlayerId(input.getPlayerId());

        // parse the response
        GoMoveDeserializer deserializer = new GoMoveDeserializer();
        GoMove move = deserializer.traverse(input.getValue());
        playerState.setMove(move);

        try {
            logic.transform(nextState, playerState);
        } catch (Exception e) {
            //LOGGER.info(String.format("Unknown response: %s", input.getValue()));
        }

        /* Determine double passes */
        int inputPlayerId = input.getPlayerId();
        if (move.getMoveType() == MoveType.PASS) {
            if (nextState.hasPreviousState()) {
                GoState checkState = nextState;
                int movesBack = 0;
                while (checkState.hasPreviousState() && movesBack < 2) {
                    checkState = (GoState) checkState.getPreviousState();
                    int checkStatePlayerId = checkState.getPlayerId();
                    if (checkStatePlayerId == inputPlayerId) {
                        ArrayList<GoPlayerState> tmpPS = checkState.getPlayerStates();
                        GoPlayerState tmpP = getActivePlayerState(tmpPS, input.getPlayerId());

                        if (tmpP.getMove().getMoveType() ==  MoveType.PASS) {
                            nextState.setDoublePass();
                        }
                    }
                    movesBack ++;
                }
            }
        }

        /* Set Ko */
        if (logic.detectKo(nextState)) {
            nextState.setKoPlayerId(input.getPlayerId());
        }

        /* Update player stats */
        playerState.setStones(nextState.getBoard().getPlayerStones(playerState.getPlayerId()));
        playerState.updateTotalStonesTaken(move.getStonesTaken());

        nextPlayerStates.get(0).setScore(logic.calculateScore(nextState.getBoard(), nextPlayerStates.get(0).getPlayerId()));
        nextPlayerStates.get(1).setScore(logic.calculateScore(nextState.getBoard(), nextPlayerStates.get(1).getPlayerId()));
        nextState.setPlayerstates(nextPlayerStates);

        return nextState;
    }

    private GoPlayerState getActivePlayerState(ArrayList<GoPlayerState> playerStates, int id) {
        for (GoPlayerState playerState : playerStates) {
            if (playerState.getPlayerId() == id) { return playerState; }
        }
        return null;
    }

    private ArrayList<GoPlayerState> clonePlayerStates(ArrayList<GoPlayerState> playerStates) {
        ArrayList<GoPlayerState> nextPlayerStates = new ArrayList<>();
        for (GoPlayerState playerState : playerStates) {
            GoPlayerState nextPlayerState = playerState.clone();
            nextPlayerStates.add(nextPlayerState);
        }
        return nextPlayerStates;
    }

    @Override
    public void sendUpdates(GoState state, GoPlayer player) {
        player.sendUpdate("round", state.getRoundNumber());
        player.sendUpdate("field", state.getBoard().toString());

        for (GoPlayerState playerState : state.getPlayerStates()) {
            GoPlayer otherPlayer = this.playerProvider.getPlayerById(playerState.getPlayerId());
            player.sendUpdate("points", otherPlayer, "" + playerState.getScore());
        }
    }

    @Override
    public boolean hasGameEnded(GoState state) {
        if (state.getRoundNumber() >= AbstractEngine.configuration.getInt("maxRounds")) return true;
        return state.isDoublePass() || logic.isBoardFull(state.getBoard()) || logic.detectKo(state);
    }

    /* Returns winner playerId, or null if there's no winner. */
    @Override
    public Integer getWinnerId(GoState state) {
        ArrayList<GoPlayerState> playerStates = state.getPlayerStates();
        Integer winnerId = null;
        double scorePlayer0 = logic.calculateScore(state.getBoard(), playerStates.get(0).getPlayerId());
        double scorePlayer1 = logic.calculateScore(state.getBoard(), playerStates.get(1).getPlayerId());
        //System.out.println("scorePlayer0 " +scorePlayer0 + " scorePlayer1 " + scorePlayer1);

        if (logic.isBoardFull(state.getBoard())) {

            if (scorePlayer0 > scorePlayer1) winnerId = playerStates.get(0).getPlayerId();
            if (scorePlayer1 > scorePlayer0) winnerId = playerStates.get(1).getPlayerId();
            return winnerId;
        }

        if (state.isDoublePass()) {

            if (scorePlayer0 > scorePlayer1) winnerId = playerStates.get(0).getPlayerId();
            if (scorePlayer1 > scorePlayer0) winnerId = playerStates.get(1).getPlayerId();
            return winnerId;

        }
        if (scorePlayer0 > scorePlayer1) return playerStates.get(0).getPlayerId();
        if (scorePlayer1 > scorePlayer0) return playerStates.get(1).getPlayerId();
        return null;
    }

    @Override
    public double getScore(GoState state) {
        return state.getRoundNumber();
    }

    @Override
    public Enum getActionType(GoState goState, AbstractPlayerState playerState) {
        return ActionType.MOVE;
    }
}
