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

import io.riddles.go.game.move.GoMove;
import io.riddles.javainterface.game.state.AbstractPlayerState;

public class GoPlayerState extends AbstractPlayerState<GoMove> {

    private int totalStonesTaken = 0;
    private int stones = 0;
    private double score = 0;
    private double komi = 0;

    public GoPlayerState(int playerId) {
        super(playerId);
    }

    public GoPlayerState clone() {
        GoPlayerState psClone = new GoPlayerState(this.playerId);
        psClone.setScore(this.score);
        psClone.setTotalStonesTaken(this.totalStonesTaken);
        psClone.setStones(this.stones);
        psClone.setKomi(this.komi);

        psClone.setPlayerId(this.playerId);
        return psClone;
    }

    public void updateTotalStonesTaken(int stonesTaken) {
        totalStonesTaken+=stonesTaken;
    }

    public void setTotalStonesTaken(int stonesTaken) {
        totalStonesTaken=stonesTaken;
    }

    public void updateScore(double s) { this.score += s; }

    public void setScore(double s) {
        this.score = s;
    }

    public void setStones(int s) {
        this.stones = s;
    }

    public void setKomi(double s) {
        this.komi = s;
    }

    public double getScore() {
        return this.score;
    }

    public double getKomi() {
        return this.komi;
    }

    public int getStones() {
        return this.stones;
    }

    public int getStonesTaken() {
        return this.totalStonesTaken;
    }

    public int getPlayerId() {
        return this.playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
}
