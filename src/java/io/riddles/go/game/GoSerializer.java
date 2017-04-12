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

package io.riddles.go.game;

import io.riddles.go.game.processor.GoProcessor;
import io.riddles.go.game.state.GoState;
import io.riddles.go.game.state.GoStateSerializer;
import org.json.JSONArray;
import org.json.JSONObject;

import io.riddles.javainterface.game.AbstractGameSerializer;

/**
 * io.riddles.catchfrauds.game.GameSerializer - Created on 8-6-16
 *
 * [description]
 *
 * @author jim
 */
public class GoSerializer extends AbstractGameSerializer<GoProcessor, GoState> {

    public GoSerializer() {
        super();
    }

    @Override
    public String traverseToString(GoProcessor processor, GoState initialState) {
        JSONObject game = new JSONObject();

        game = addDefaultJSON(initialState, game, processor);

        JSONObject field = new JSONObject();
        field.put("width", initialState.getBoard().getWidth());
        field.put("height", initialState.getBoard().getHeight());

        game.getJSONObject("settings").put("field", field);

        // put all states
        GoStateSerializer serializer = new GoStateSerializer();
        JSONArray states = new JSONArray();

        // DIRTY FIX BECAUSE OF THIS STUPID ASS TurnBasedGameLoop
        JSONObject initialJsonState = serializer.traverseToJson(initialState);
        initialJsonState.put("round", initialState.getRoundNumber() - 1);
        states.put(initialJsonState);

        GoState state = initialState;
        int counter = 0;
        while (state.hasNextState()) {
            state = (GoState) state.getNextState();
            JSONObject jsonState = serializer.traverseToJson(state);

            // DIRTY FIX BECAUSE OF THIS STUPID ASS TurnBasedGameLoop
            if (counter % 2 == 1) {
                jsonState.put("round", state.getRoundNumber() - 1);
            }
            counter++;

            states.put(jsonState);
        }

        game.put("states", states);

        return game.toString();
    }
}
