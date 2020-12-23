package edu.rice.comp504.model.strategy;

import edu.rice.comp504.model.Game;
import edu.rice.comp504.model.paintobject.Ghost;

import java.awt.*;

/**
 * The ghost will directly go home and then come out with original strategy.
 */
public class GoHomeStrategy implements GhostStrategy {
    GhostStrategy originalStrategy;

    /**
     * Constructor.
     */
    public GoHomeStrategy(GhostStrategy original) {
        originalStrategy = original;
    }

    /**
     * Get the strategy name.
     *
     * @return The strategy name
     */
    public String getName() {
        return "go_home";
    }

    /**
     * Update the ghost using the behavior defined by the strategy.
     */
    public void update(Ghost ghost) {
        Point dstPos = ghost.getStartPos();
        Point srcPos = ghost.getPos();
        if (dstPos.equals(srcPos)) {
            ghost.setStrategy(originalStrategy);
            ghost.setStatus("normal");
        }
        String direction = Game.getOnly().findPath(srcPos, dstPos);
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
