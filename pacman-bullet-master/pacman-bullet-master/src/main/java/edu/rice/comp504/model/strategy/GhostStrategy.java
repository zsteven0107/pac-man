package edu.rice.comp504.model.strategy;

import edu.rice.comp504.model.paintobject.Ghost;

/**
 * Strategies will implement this interface.
 */
public interface GhostStrategy {
    /**
     * Get the strategy name.
     *
     * @return The strategy name
     */
    public String getName();

    /**
     * Update the ghost using the behavior defined by the strategy.
     */
    public void update(Ghost ghost);
}
