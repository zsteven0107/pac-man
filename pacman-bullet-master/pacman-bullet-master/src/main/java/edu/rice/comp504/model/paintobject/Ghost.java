package edu.rice.comp504.model.paintobject;


import edu.rice.comp504.model.strategy.GhostStrategy;

import java.awt.*;

/**
 * The ghost.
 */
public class Ghost extends PaintObject {

    private final Point startPos;
    private String direction;
    private GhostStrategy strategy;
    private boolean isVisible = true;
    /**
     * "normal","darkBlue","eye".
     */
    private String status;
    private String color;
    private final Integer darkBlueTimer = 25;
    private Integer ghostCycleTimer = 3;
    private Integer ghostDarbBlueCount;
    private static Integer score;

    /**
     * Constructor.
     */
    public Ghost(Point startPos, String color, GhostStrategy strategy) {
        this.startPos = startPos;
        this.pos = startPos;
        direction = "up";
        this.strategy = strategy;
        status = "normal";
        this.color = color;
        score = 200;
        this.ghostDarbBlueCount = this.darkBlueTimer;
    }

    /**
     * Set the position of the ghost to the starting position.
     */
    public void returnStartPos() {
        pos = startPos;
    }

    /**
     * get the starting position of the ghost.
     */
    public Point getStartPos() {
        return startPos;
    }

    /**
     * get the current position of the ghost.
     */
    public Point getPos() {
        return pos;
    }

    /**
     * Set the status of the ghost.
     */
    public void setStatus(String newStatus) {
        status = newStatus;
    }

    /**
     * .
     */
    public void setStrategy(GhostStrategy newStrategy) {
        strategy = newStrategy;
    }

    /**
     * Get the moving strategy of the ghost.
     */
    public GhostStrategy getStrategy() {
        return strategy;
    }

    /**
     * Set the score of the ghost.
     */
    public static void setScore(int newScore) {
        score = newScore;
    }

    /**
     * Get the score of the ghost.
     */
    public static Integer getScore() {
        return score;
    }

    /**
     * Update the position of the ghost according to the strategy.
     */
    public void updatePos(Integer timer) {
        if (this.status.equals("flash")) {
            this.isVisible = !this.isVisible;
            if (timer % this.ghostCycleTimer == 0) {
                this.strategy.update(this);
                this.ghostDarbBlueCount--;
            }
            if (this.ghostDarbBlueCount == 0) {
                this.ghostDarbBlueCount = this.darkBlueTimer;
                this.setStatus("normal");
                this.setScore(200);
                this.isVisible = true;
            }
        } else if (this.status.equals("eyes")) {
            this.strategy.update(this);
            this.ghostDarbBlueCount = this.darkBlueTimer;
            this.setScore(200);
            this.isVisible = true;
        } else if (this.status.equals("normal")) {
            if (timer % this.ghostCycleTimer == 0) {
                this.strategy.update(this);
            }
        }
    }

    /**
     * get Direction.
     */
    public String getDirection() {
        return this.direction;
    }

    /**
     * Set direction.
     */
    public void setDirection(String direction) {
        this.direction = direction;
    }

    /**
     * Get the status of the ghost.
     */
    public String getStatus() {
        return status;
    }

    /**
     * Get the cycle timer.
     */
    public Integer getGhostCycleTimer() {
        return ghostCycleTimer;
    }
}
