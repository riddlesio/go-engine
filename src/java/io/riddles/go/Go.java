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

package io.riddles.go;

import io.riddles.go.engine.GoEngine;
import io.riddles.go.game.state.GoState;
import io.riddles.javainterface.game.player.PlayerProvider;
import io.riddles.javainterface.io.IOHandler;

/**
 * io.riddles.go.Go - Created on 2-6-16
 *
 * [description]
 *
 * @author jim
 */
public class Go {

    public static void main(String[] args) throws Exception {
        GoEngine engine;
        engine = new GoEngine(new PlayerProvider<>(), new IOHandler());

        GoState initialState = engine.willRun();
        GoState finalState = engine.run(initialState);
        engine.didRun(initialState, finalState);
    }
}