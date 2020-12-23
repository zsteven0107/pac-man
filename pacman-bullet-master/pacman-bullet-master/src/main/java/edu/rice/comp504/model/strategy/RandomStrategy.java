package edu.rice.comp504.model.strategy;

import edu.rice.comp504.model.Game;
import edu.rice.comp504.model.paintobject.Ghost;

import java.awt.*;

/**
 * The ghost will choose a directly way to go at the crossroads.
 */
public class RandomStrategy implements GhostStrategy {
    Point target = null;

    /**
     * Constructor.
     */
    public RandomStrategy() {
        // do not init target in the constructor
    }

    /**
     * Get the strategy name.
     *
     * @return The strategy name
     */
    public String getName() {
        return "random";
    }

    /**
     * Update the ghost using the behavior defined by the strategy.
     */
    public void update(Ghost ghost) {

        Game game = Game.getOnly();
        Point srcPos = ghost.getPos();
        if (target == null || srcPos.equals(target)) {
            target = game.getRandomPos();
        }
        String dir = game.findPath(srcPos, target);
        int x;
        int y;
        switch (dir) {
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
            case "right":
                x = srcPos.x + 1;
                y = srcPos.y;
                break;
            default:
                // bad target, let's try again
                target = game.getRandomPos();
                update(ghost);
                return;
        }
        ghost.setPos(new Point(x, y));
    }


}
