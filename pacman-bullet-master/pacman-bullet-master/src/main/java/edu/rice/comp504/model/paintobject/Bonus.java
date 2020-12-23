package edu.rice.comp504.model.paintobject;

import edu.rice.comp504.model.Game;

import java.awt.*;

/**
 * The bonus.
 */
public class Bonus extends PaintObject {
    private Boolean isVisible;
    private String type;
    private Integer score;
    private Integer fruitTimer = 100;

    /**
     * Constructor.
     *
     * @param pos   position of bonus
     * @param type  type of bonus -> "small", "big", "fruit"
     * @param score score of bonus -> 10, 50, 100
     */
    public Bonus(Point pos, String type, Integer score) {
        this.pos = pos;
        this.isVisible = true;
        this.type = type;
        this.score = score;
    }

    /**
     * Update the position of the object.
     */
    public void updatePos(Integer timer) {
        if (this.type == "fruit" && timer % this.fruitTimer == 0) {
            this.pos = Game.getOnly().getRandomPos();
            this.appear();
        }
    }

    /**
     * The bonus appears.
     */
    public void appear() {
        isVisible = true;
    }

    /**
     * The bonus disappears.
     */
    public void disappear() {
        isVisible = false;
    }

    /**
     * Tell if the bonus is visible.
     */
    public Boolean isVisible() {
        return isVisible;
    }

    /**
     * Get type.
     */
    public String getType() {
        return type;
    }

    public int getScore() {
        return score;
    }
}
