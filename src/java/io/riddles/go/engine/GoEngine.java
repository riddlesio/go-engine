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

package io.riddles.go.engine;

import java.util.ArrayList;

import io.riddles.go.game.board.GoBoard;
import io.riddles.go.game.state.GoPlayerState;
import io.riddles.go.game.processor.GoProcessor;
import io.riddles.go.game.state.GoState;
import io.riddles.go.game.player.GoPlayer;
import io.riddles.javainterface.configuration.Configuration;
import io.riddles.javainterface.engine.AbstractEngine;
import io.riddles.go.game.GoSerializer;
import io.riddles.javainterface.engine.TurnBasedGameLoop;
import io.riddles.javainterface.exception.TerminalException;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.BotIOInterface;

import io.riddles.javainterface.io.IOHandler;

/**
 * io.riddles.go.engine.GoEngine - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost- joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class GoEngine extends AbstractEngine<GoProcessor, GoPlayer, GoState> {

    public GoEngine(PlayerProvider<GoPlayer> playerProvider, IOHandler ioHandler) throws TerminalException {
        super(playerProvider, ioHandler);
    }

    @Override
    protected Configuration getDefaultConfiguration() {
        Configuration cc = new Configuration();
        cc.put("maxRounds", 200);
        cc.put("fieldWidth", 19);
        cc.put("fieldHeight", 19);
        cc.put("komi", 7.5);
        return cc;
    }

    @Override
    protected GoProcessor createProcessor() {
        return new GoProcessor(playerProvider);
    }

    @Override
    protected TurnBasedGameLoop createGameLoop() {
        return new TurnBasedGameLoop();
    }

    @Override
    protected GoPlayer createPlayer(int id) {
        return new GoPlayer(id);
    }

    @Override
    protected void sendSettingsToPlayer(GoPlayer player) {
        player.sendSetting("your_botid", player.getId());
        player.sendSetting("field_width", configuration.getInt("fieldWidth"));
        player.sendSetting("field_height", configuration.getInt("fieldHeight"));
        player.sendSetting("max_rounds", configuration.getInt("maxRounds"));
    }

    @Override
    protected String getPlayedGame(GoState initialState) {
        GoSerializer serializer = new GoSerializer();
        return serializer.traverseToString(this.processor, initialState);
    }

    @Override
    protected GoState getInitialState() {
        ArrayList<GoPlayerState> goPlayerStates = new ArrayList<>();

        for (GoPlayer player : this.playerProvider.getPlayers()) {
            GoPlayerState goPlayerState = new GoPlayerState(player.getId());
            goPlayerStates.add(goPlayerState);
        }
        goPlayerStates.get(goPlayerStates.size() - 1).setKomi(configuration.getDouble("komi"));

        GoState state = new GoState(null, goPlayerStates, 0);
        state.setPlayerId(playerProvider.getPlayers().get(0).getId());

        int fieldWidth = configuration.getInt("fieldWidth");
        int fieldHeight = configuration.getInt("fieldHeight");

        GoBoard board = new GoBoard(fieldWidth, fieldHeight);
        state.setBoard(board);

        return state;
    }

}
