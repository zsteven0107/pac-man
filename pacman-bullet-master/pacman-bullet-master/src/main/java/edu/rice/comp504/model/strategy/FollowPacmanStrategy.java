package edu.rice.comp504.model.strategy;

import edu.rice.comp504.model.Game;
import edu.rice.comp504.model.paintobject.Ghost;

import java.awt.*;

/**
 * The ghost will always follow the pacman.
 */
public class FollowPacmanStrategy implements GhostStrategy {

    public FollowPacmanStrategy() {
    }

    /**
     * Get the strategy name.
     *
     * @return The strategy name
     */
    public String getName() {
        return "follow_pacman";
    }

    /**
     * Update the ghost using the behavior defined by the strategy.
     */
    public void update(Ghost ghost) {
        Point decPos = Game.getOnly().getPacmanPos();
        Point srcPos = ghost.getPos();
        String direction = Game.getOnly().findPath(srcPos, decPos);
        int x;
        int y;
        switch (direction) {
            case "up":
                x = srcPos.x;
                y = srcPos.y - 1;
                break;
            case "down":
                x = srcPos.x;
                y = srcPos.y + 1;
                break;
            case "left":
                x = srcPos.x - 1;
                y = srcPos.y;
                break;
            default:
                x = srcPos.x + 1;
                y = srcPos.y;
                break;
        }
        ghost.setPos(new Point(x, y));
    }


}
