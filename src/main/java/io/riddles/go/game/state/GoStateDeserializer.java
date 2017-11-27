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

package io.riddles.go.game.state;

import io.riddles.go.game.player.GoPlayer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.riddles.javainterface.game.state.AbstractStateDeserializer;

/**
 * io.riddles.go.game.state.GoStateDeserializer - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost de Meij - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class GoStateDeserializer extends
        AbstractStateDeserializer<GoPlayer, GoState> {

    public GoStateDeserializer(ArrayList<GoPlayer> players) {
        super(players);
    }

    @Override
    public GoState traverse(String statesString) throws JSONException {
        GoState state = null;
        Object states = new JSONObject(statesString);

        if (states instanceof JSONArray) {
            JSONArray statesJson = (JSONArray) states;
            for (int i = 0; i < statesJson.length(); i++) {
                JSONObject stateJson = statesJson.getJSONObject(i);
                state = visitState(stateJson, state);
            }
        } else if (states instanceof JSONObject) {
            JSONObject stateJson = (JSONObject) states;
            state = visitState(stateJson, null);
        } else {
            throw new JSONException("Input string is not array and not object");
        }

        return state;
    }

    private GoState visitState(JSONObject stateJson, GoState previousState) throws JSONException {
        int roundNumber = stateJson.getInt("round");
        return null;
    }

}
