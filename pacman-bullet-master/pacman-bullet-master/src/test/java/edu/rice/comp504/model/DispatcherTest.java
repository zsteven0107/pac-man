package edu.rice.comp504.model;

import edu.rice.comp504.model.paintobject.Ghost;
import edu.rice.comp504.model.paintobject.Pacman;
import edu.rice.comp504.model.response.UpdateResponse;
import org.junit.Test;

import java.awt.*;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test the dispatcher.
 */
public class DispatcherTest {

    /**
     * Update n times and return the response.
     */
    private UpdateResponse updateNTimes(DispatchAdapter da, Integer n) {
        for (int i = 0; i < n - 1; ++i) {
            da.updateGame();
        }
        return da.updateGame();
    }

    @Test
    public void pacmanMoveTest() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        Pacman pacman = game.getPacman();
        Point start = pacman.getPos();
        UpdateResponse res = updateNTimes(da, pacman.getPacmanCycleTimer() * 2);
        assertEquals(new Point(start.x - 2, start.y), res.getPacman().getPos());
    }

    @Test
    public void pacmanEatBonus() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        Pacman pacman = game.getPacman();
        Point start = pacman.getPos();
        UpdateResponse res = updateNTimes(da, pacman.getPacmanCycleTimer() * 7);
        assertEquals(10, res.getScore());
    }

    @Test
    public void pacmanCollideWithWall() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        Pacman pacman = game.getPacman();
        Point start = pacman.getPos();
        UpdateResponse res = updateNTimes(da, pacman.getPacmanCycleTimer() * 22);
        assertEquals(new Point(2, start.y), res.getPacman().getPos());
    }

    @Test
    public void pacmanTurnDirection() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        Pacman pacman = game.getPacman();
        Point start = pacman.getPos();
        da.userControl("right");
        UpdateResponse res = updateNTimes(da, pacman.getPacmanCycleTimer());
        assertEquals(new Point(start.x + 1, start.y), res.getPacman().getPos());
        da.userControl("left");
        res = updateNTimes(da, pacman.getPacmanCycleTimer() * 7);
        assertEquals(new Point(start.x - 6, start.y), res.getPacman().getPos());
        da.userControl("up");
        res = updateNTimes(da, pacman.getPacmanCycleTimer());
        assertEquals(new Point(start.x - 6, start.y - 1), res.getPacman().getPos());
        da.userControl("down");
        res = updateNTimes(da, pacman.getPacmanCycleTimer());
        assertEquals(new Point(start.x - 6, start.y), res.getPacman().getPos());
    }

    @Test
    public void ghostChasePacman() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        List<Ghost> ghosts = game.getGhosts();
        UpdateResponse res = updateNTimes(da, ghosts.get(0).getGhostCycleTimer() * 27);
        Point p = game.getPacmanPos();
        assertEquals(new Point(p.x + 1, p.y), ghosts.get(0).getPos());
    }

    @Test
    public void pacmanEatBigDots() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        List<Ghost> ghosts = game.getGhosts();
        Pacman pacman = game.getPacman();
        updateNTimes(da, pacman.getPacmanCycleTimer() * 20);
        ghosts.forEach(ghost -> {
            assertEquals(ghost.getStatus(), "flash");
        });

        updateNTimes(da, ghosts.get(0).getGhostCycleTimer() * 6);
        da.userControl("right");
        // pacman eats ghost
        updateNTimes(da, ghosts.get(0).getGhostCycleTimer() * 4);
        assertEquals(ghosts.get(0).getStatus(), "eyes");

        updateNTimes(da, ghosts.get(0).getGhostCycleTimer() * 10);
    }

    @Test
    public void pacmanLoseLife() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        List<Ghost> ghosts = game.getGhosts();
        Pacman pacman = game.getPacman();

        assertEquals("3", game.getLife().toString());
        //lose life
        updateNTimes(da, ghosts.get(0).getGhostCycleTimer() * 40);
        assertEquals("2", game.getLife().toString());

        //start new game
        updateNTimes(da, ghosts.get(0).getGhostCycleTimer() * 60);
        assertEquals("3", game.getLife().toString());
    }

    @Test
    public void wrongUserControlInput() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        Pacman pacman = game.getPacman();
        Point start = pacman.getPos();
        da.userControl("wrong");
        UpdateResponse res = updateNTimes(da, pacman.getPacmanCycleTimer());
        assertEquals(new Point(start.x - 1, start.y), res.getPacman().getPos());

        game.getResponse();
    }

    @Test
    public void initGame() {
        DispatchAdapter da = new DispatchAdapter();
        Game game = da.initGame();
        UpdateResponse res = updateNTimes(da, 1);
        assertEquals("0", game.getDifficulty().toString());
        assertEquals("3", game.getLife().toString());
    }
}