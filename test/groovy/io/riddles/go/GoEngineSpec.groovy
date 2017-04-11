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

package io.riddles.go

import io.riddles.go.engine.GoEngine
import io.riddles.go.game.player.GoPlayer
import io.riddles.go.game.processor.GoProcessor
import io.riddles.go.game.state.GoState
import io.riddles.javainterface.game.player.PlayerProvider
import io.riddles.javainterface.game.state.AbstractState
import io.riddles.javainterface.io.FileIOHandler
import spock.lang.Specification
import spock.lang.Ignore


/**
 * io.riddles.go.engine.GoEngineSpec - Created on 8-6-16
 *
 * [description]
 *
 * @author joost
 */

class GoEngineSpec extends Specification {

    public static class TestEngine extends GoEngine {

        TestEngine(PlayerProvider<GoEngine> playerProvider, String wrapperInput) {
            super(playerProvider, null);
            this.ioHandler = new FileIOHandler(wrapperInput);
        }
    }

    //@Ignore
    def "test a standard game"() {
        println("test a standard game")

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_input.txt"
        botInputs[0] = "./src/test/resources/bot1_input.txt"
        botInputs[1] = "./src/test/resources/bot2_input.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(0);
        player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(1);
        player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)

        AbstractState state = engine.willRun()
        state = engine.run(state);
        /* Fast forward to final state */
        while (state.hasNextState()) state = state.getNextState();

        state.getBoard().dumpBoard();
        GoProcessor processor = engine.getProcessor();

        expect:
        state instanceof GoState;
        state.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,1,1,1,1,1,1,1,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,0,0,0,0,0,0,0,.,.,.,.,.,.,.,.,.,.,1,.,0,0,0,0,0,0,0,.,.,.,.,.,.,.,.,.,.,1,.,0,0,0,0,0,0,0,.,.,.,.,.,.,.,.,.,0,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
        processor.getWinnerId(state) == null;

    }

    //@Ignore
    def "test the Ko Rule"() {

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_inputshort.txt"
        botInputs[0] = "./src/test/resources/bot1_input_korule.txt"
        botInputs[1] = "./src/test/resources/bot2_input_korule.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(1); player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(2); player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)


        AbstractState initialState = engine.willRun()
        AbstractState finalState = engine.run(initialState);
        engine.didRun(initialState, finalState);
        GoProcessor processor = engine.getProcessor();

        expect:
        finalState.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,1,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
        processor.getWinnerId(finalState) == 1;

    }

    //@Ignore
    def "test illegal placement moves"() {

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_inputshort.txt"
        botInputs[0] = "./src/test/resources/bot1_input_illegal.txt"
        botInputs[1] = "./src/test/resources/bot2_input_illegal.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(1); player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(2); player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)

        AbstractState state = engine.willRun()
        state = engine.run(state);
        /* Fast forward to final state */
        while (state.hasNextState()) state = state.getNextState();

        state.getBoard().dumpBoard();
        GoProcessor processor = engine.getProcessor();

        expect:
        state.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
        processor.getWinnerId(state) == 1;

    }

    //@Ignore
    def "test double pass moves"() {

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_input.txt"
        botInputs[0] = "./src/test/resources/bot_doublepass_input.txt"
        botInputs[1] = "./src/test/resources/bot2_input_illegal.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(1); player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(2); player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)

        AbstractState state = engine.willRun()
        state = engine.run(state);
        /* Fast forward to final state */
        while (state.hasNextState()) state = state.getNextState();

        GoProcessor processor = engine.getProcessor();

        expect:
        state.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
        processor.getWinnerId(state) == null;
    }

    //@Ignore
    def "test testgame"() {

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_inputshort.txt"
        botInputs[0] = "./src/test/resources/bot1_input_testgame.txt"
        botInputs[1] = "./src/test/resources/bot2_input_testgame.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(3); player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(5); player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)

        AbstractState state = engine.willRun()
        state = engine.run(state);
        /* Fast forward to final state */
        while (state.hasNextState()) state = state.getNextState();

        println("final board");
        state.getBoard().dumpBoard();
        GoProcessor processor = engine.getProcessor();

        expect:
        state.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,5,.,.,.,3,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,3,.,.,.,3,.,.,3,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,3,.,.,.,.,.,.,.,.,.,.,.,5,.,.,.,.,.,.,.,.,3,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,3,.,.,.,.,3,.,.,.,.,.,5,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,3,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,3,.,.,.,.,.,.,.,.,.,.,.,.,.,5,3,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,5,.,.,.,.,.,.,5,.,.,.,.,.,.,.,.,.,.,.,.,.,.,5,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,3,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
        processor.getWinnerId(state) == null;
    }

    //@Ignore
    def "test JSON"() {

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_inputshort.txt"
        botInputs[0] = "./src/test/resources/bot1_input_testgame.txt"
        botInputs[1] = "./src/test/resources/bot2_input_testgame.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(1); player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(2); player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)

        AbstractState initialState = engine.willRun()
        AbstractState finalState = engine.run(initialState);
        engine.didRun(initialState, finalState);

        expect:
        finalState.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,1,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,1,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,2,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
    }

    //@Ignore
    def "test JSON Score"() {

        setup:
        String[] botInputs = new String[2]
        def wrapperInput = "./src/test/resources/wrapper_inputshort.txt"
        botInputs[0] = "./src/test/resources/bot1_input_score.txt"
        botInputs[1] = "./src/test/resources/bot2_input_score.txt"

        PlayerProvider<GoPlayer> playerProvider = new PlayerProvider<>();
        GoPlayer player1 = new GoPlayer(1); player1.setIoHandler(new FileIOHandler(botInputs[0])); playerProvider.add(player1);
        GoPlayer player2 = new GoPlayer(2); player2.setIoHandler(new FileIOHandler(botInputs[1])); playerProvider.add(player2);

        def engine = new TestEngine(playerProvider, wrapperInput)

        AbstractState initialState = engine.willRun()
        AbstractState finalState = engine.run(initialState);
        engine.didRun(initialState, finalState);
        GoProcessor processor = engine.getProcessor();

        expect:
        finalState.getBoard().toString() == ".,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,1,1,1,.,2,.,.,2,.,.,.,.,.,.,.,.,.,.,1,.,.,1,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,1,1,1,1,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,1,.,.,.,.,.,.,.,.,.,.,.,.,.,.,2,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.,.";
        processor.getWinnerId(finalState) == 1;
    }
}