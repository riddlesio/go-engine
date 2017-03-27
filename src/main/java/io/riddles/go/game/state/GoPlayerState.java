package io.riddles.go.game.state;

import io.riddles.go.game.move.GoMove;
import io.riddles.javainterface.game.state.AbstractPlayerState;

/**
 * Created by Niko on 23/11/2016.
 */
public class GoPlayerState extends AbstractPlayerState<GoMove> {

    int totalStonesTaken = 0;
    int stones = 0;
    double score = 0;
    double komi = 0;


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
    public void setStones(int s) { this.stones = s; }
    public void setKomi(double s) { this.komi = s; }


    public double getScore() { return this.score; }
    public double getKomi() { return this.komi; }

    public int getStones() { return this.stones; }
    public int getStonesTaken() { return this.totalStonesTaken; }

    public int getPlayerId() { return this.playerId; }
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

}
