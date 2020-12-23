package edu.rice.comp504.model.response;

import edu.rice.comp504.model.paintobject.Bonus;
import edu.rice.comp504.model.paintobject.Ghost;
import edu.rice.comp504.model.paintobject.Pacman;
import lombok.Builder;

/**
 * The response generated for the game.
 */
@Builder
public class UpdateResponse {
    String event;
    int life;
    int difficulty;
    int score;
    Pacman pacman;
    Ghost[] ghosts;
    Bonus[] bonuses;
    int[][] map;

    /**
     * Get the pacman.
     */
    public Pacman getPacman() {
        return pacman;
    }

    /**
     * Get the score.
     */
    public int getScore() {
        return score;
    }
}
