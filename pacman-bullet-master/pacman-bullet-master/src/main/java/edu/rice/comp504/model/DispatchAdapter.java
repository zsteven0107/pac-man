package edu.rice.comp504.model;


import edu.rice.comp504.model.response.UpdateResponse;

/**
 * The dispatch adapter is responsible for ensuring that the game is properly updated.
 */
public class DispatchAdapter {

    /**
     * Handles the /update request.
     */
    public UpdateResponse updateGame() {
        return Game.getOnly().update();
    }

    /**
     * Handles the /init_game request.
     */
    public Game initGame() {
        Game.reset();
        return Game.getOnly();
    }

    /**
     * Handles the /control request.
     */
    public Object userControl(String req) {
        Game.getOnly().getPacman().setDirection(req);
        return null;
    }
}
