package edu.rice.comp504.model.paintobject;


import edu.rice.comp504.model.Game;

import java.awt.*;

/**
 * The pacman.
 */
public class Pacman extends PaintObject {
    private final Point startPos;
    private String direction;
    private Integer pacmanCycleTimer = 1;

    /**
     * Constructor.
     */
    public Pacman(Point startPos) {

        this.startPos = startPos;
        this.pos = this.startPos;
        this.direction = "left";
    }

    /**
     * Move forward.
     */
    private Point getNextPos(String dir, Point p) {
        Point nextPos;
        switch (dir) {
            case "up":
                nextPos = new Point(p.x, p.y - 1);
                break;
            case "down":
                nextPos = new Point(p.x, p.y + 1);
                break;
            case "left":
                nextPos = new Point(p.x - 1, p.y);
                break;
            case "right":
                nextPos = new Point(p.x + 1, p.y);
                break;
            default:
                System.out.println("Error: bad direction from front end " + dir);
                return null;
        }
        return nextPos;
    }

    /**
     * Set direction.
     */
    public void setDirection(String inputDir) {
        if (inputDir.equals(this.direction)) {
            return;
        }
        if (!inputDir.equals("up") && !inputDir.equals("down") && !inputDir.equals("left") && !inputDir.equals("right")) {
            return;
        }
        Game game = Game.getOnly();
        Point nextPos = getNextPos(inputDir, pos);
        if (game.isValidPos(nextPos) == false || game.isWall(nextPos)) {
            return;
        }
        this.direction = inputDir;
    }

    /**
     * Update the position of the pacman according to the direction.
     */
    public void updatePos(Integer timer) {
        if (timer % this.pacmanCycleTimer == 0) {
            if (direction.equals("up")) {
                if (Game.getOnly().isWall(new Point(pos.x, pos.y - 1)) == false) {
                    this.setPos(new Point(pos.x, pos.y - 1));
                    return;
                }
            }

            if (direction.equals("right")) {
                if (Game.getOnly().getExitRightPos().equals(pos)) {
                    pos = Game.getOnly().getExitLeftPos();
                }
                if (Game.getOnly().isWall(new Point(pos.x + 1, pos.y)) == false) {
                    this.setPos(new Point(pos.x + 1, pos.y));
                    return;
                }
            }

            if (direction.equals("down")) {
                if (Game.getOnly().isWall(new Point(pos.x, pos.y + 1)) == false) {
                    this.setPos(new Point(pos.x, pos.y + 1));
                    return;
                }
            }

            if (direction.equals("left")) {
                if (Game.getOnly().getExitLeftPos().equals(pos)) {
                    pos = Game.getOnly().getExitRightPos();
                }
                if (Game.getOnly().isWall(new Point(pos.x - 1, pos.y)) == false) {
                    this.setPos(new Point(pos.x - 1, pos.y));
                    return;
                }
            }
        }
    }

    /**
     * Set the pacman's position to the starting postion.
     */
    public void returnStartPos() {
        pos = startPos;
    }

    /**
     * Tell if the pacman collides with another object.
     */
    public Boolean samePosWith(PaintObject other) {
        return pos.equals(other.getPos());
    }

    /**
     * Get the timer.
     */
    public Integer getPacmanCycleTimer() {
        return pacmanCycleTimer;
    }
}
