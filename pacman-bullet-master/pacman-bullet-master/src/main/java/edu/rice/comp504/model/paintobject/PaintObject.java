package edu.rice.comp504.model.paintobject;


import java.awt.*;

/**
 * A object in the map.
 */
public abstract class PaintObject {
    protected Point pos;
    protected Integer cycle;
    protected Integer t;

    public abstract void updatePos(Integer timer);

    /**
     * Get the position of the object.
     */
    public Point getPos() {
        return pos;
    }

    /**
     * Set the position of the object.
     */
    public void setPos(Point newPos) {
        pos = newPos;
    }
}
