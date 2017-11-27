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

package io.riddles.go.game.move;

import io.riddles.go.game.board.Point;
import io.riddles.javainterface.game.move.AbstractMove;

/**
 * io.riddles.go.game.move.GoMove - Created on 6/27/16
 *
 * [description]
 *
 * @author Joost - joost@riddles.io, Jim van Eeden - jim@riddles.io
 */
public class GoMove extends AbstractMove {

    private MoveType type;
    private Point coordinate;
    private int stonesTaken;

    public GoMove(MoveType type, Point c) {
        super();
        this.type = type;
        this.coordinate = c;
    }

    public GoMove(Exception exception) {
        super(exception);
    }

    public MoveType getMoveType() {
        return this.type;
    }

    public String toString() {
        if (this.type == null) return null;

        return "" + this.type;
    }

    public void setCoordinate(Point p) { this.coordinate = p; }
    public Point getCoordinate() { return this.coordinate; }

    public void setStonesTaken(int stonesTaken) { this.stonesTaken = stonesTaken; }
    public int getStonesTaken() { return this.stonesTaken; }

}
