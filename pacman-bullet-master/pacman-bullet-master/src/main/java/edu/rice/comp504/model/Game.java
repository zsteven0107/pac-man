package edu.rice.comp504.model;

import edu.rice.comp504.model.paintobject.Bonus;
import edu.rice.comp504.model.paintobject.Ghost;
import edu.rice.comp504.model.paintobject.Pacman;
import edu.rice.comp504.model.response.UpdateResponse;
import edu.rice.comp504.model.strategy.FollowPacmanStrategy;
import edu.rice.comp504.model.strategy.GoHomeStrategy;
import edu.rice.comp504.model.strategy.RandomStrategy;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.*;


/**
 * The Pacman game.
 */
public class Game {
    private Integer life;
    private Integer difficulty;
    private Integer score;
    private Integer timer;

    //0: wall, 1: passageway
    private int[][] map;
    private Pacman pacman;
    private List<Ghost> ghosts;
    private Bonus fruit;
    private List<Bonus> bonuses;
    private String event;
    private Point exitLeftPos;
    private Point exitRightPos;
    final int wall = 0;
    final int wall1 = -1;
    HashMap<Point, HashMap<Point, String>> pathDict;
    private static Game ONLY = null;
    String maze = "#########################################################\n" +
            "#########################################################\n" +
            "##@...................................................@##\n" +
            "##.###.###################.##############.##.%%.##.###.##\n" +
            "##.###.###################.##############.##.%%.##.###.##\n" +
            "##.###....................................##.%%.##.###.##\n" +
            "##.###.%%%%%%%%.##########.%%.%%.########.##.%%.##.###.##\n" +
            "##.###.%%%%%%%%.##########.%%.%%.########.##.%%.##.###.##\n" +
            "##.....%%    %%.##########.%%.%%.............%%........##\n" +
            "######.%%    %%            %%.%%.%%%%%%%%.%%%%%%%%.######\n" +
            "######.%%%%%%%% %%%%-*%%%% %%.%%.%%%%%%%%.%%%%%%%%.######\n" +
            "L     .%%%%%%%% %%%%--%%%% %%.%%.%%    %%....%%....     R\n" +
            "######.%%%%%%%% %%      %% %%.%%.%%%%%%%%.##.%%.##.######\n" +
            "######.%%    %% %%-*-*-*%% %%.%%.%%%%%%%%.##.%%.##.######\n" +
            "##.....%%    %% %%      %% %%.%%.%%.......##.%%.##.....##\n" +
            "##.###.%%%%%%%% %%%%%%%%%% %%.%%.%%%%%%%%.##.%%.##.###.##\n" +
            "##.###.%%%%%%%% %%%%%%%%%% %%.%%.%%%%%%%%.##.%%.##.###.##\n" +
            "##@............      $     ...........................@##\n" +
            "#########################################################\n" +
            "#########################################################\n";

    /**
     * Get the singleton.
     */
    public static Game getOnly() {
        if (ONLY == null) {
            ONLY = new Game();
        }
        return ONLY;
    }

    /**
     * The private constructor.
     */
    private Game() {
        exitLeftPos = null;
        exitRightPos = null;
        ghosts = new LinkedList<>();
        bonuses = new LinkedList<>();
        buildMap("maze1");
        pathDict = new HashMap<>();
        newGame();
    }

    /**
     * Return the ghosts.
     */
    public List<Ghost> getGhosts() {
        return ghosts;
    }

    /**
     * New game, initialize necessary members. Used by thee constructor.
     */
    public void newGame() {
        for (Bonus b : bonuses) {
            if (b.getType().equals("fruit") == false) {
                b.appear();
            }
        }
        pacman.returnStartPos();
        for (Ghost ghost : ghosts) {
            ghost.returnStartPos();
        }
        life = 3;
        difficulty = 0;
        score = 0;
        timer = 0;
    }

    /**
     * Pacman eats all the dots and level up.
     */
    private void levelUp() {
        difficulty += 1;
        event = "level_up";
        pacman.returnStartPos();
        for (Ghost ghost : ghosts) {
            ghost.returnStartPos();
        }
        for (Bonus b : bonuses) {
            if (b.getType() != "fruit") {
                b.appear();
            }
        }
    }

    /**
     * The pacman loses a life.
     */
    private void loseLife() {
        life -= 1;
        if (life == 0) {
            event = "game_over";
            newGame();
            return;
        }
        event = "lose_life";
        pacman.returnStartPos();
        for (Ghost ghost : ghosts) {
            ghost.returnStartPos();
        }
    }

    /**
     * Build the map and crucial parts according to the map file.
     */
    private void buildMap(String mazeName) {
        // for deploy on heroku
        String fileName = "/app/target/classes/public/maze/" + mazeName + ".txt";

        // for local test
        //String fileName = System.getProperty("user.dir") + "/src/main/java/edu/rice/comp504/maze/" + mazeName + ".txt";
        System.out.println(fileName);

        try {
            //read maze from file into a list
            //File file = new File(fileName);
            //Scanner sc = new Scanner(file);
            LinkedList<String> list = new LinkedList();

//            while (sc.hasNextLine()) {
//                String line = sc.nextLine();
//                if (line != "") {
//                    list.add(line);
//                }
//            }

            list = new LinkedList<String>(Arrays.asList(maze.split("\n")));

            //initialize map
            map = new int[list.size()][list.get(0).length()];

            //ghost color and ghost count
            String[] colors = new String[]{"red", "blue", "pink", "yellow"};

            int len = list.get(0).length();

            for (int i = 0; i < list.size(); i++) {
                String line = list.get(i);
                while (line.length() < len) {
                    line += ' ';
                }
                for (int j = 0; j < line.length(); j++) {
                    char ch = line.charAt(j);
                    map[i][j] = 1;
                    switch (ch) {
                        case '#': {
                            map[i][j] = wall;
                            break;
                        }
                        case '%': {
                            map[i][j] = wall1;
                            break;
                        }
                        case '.': {
                            bonuses.add(new Bonus(new Point(j, i), "small", 10));
                            break;
                        }
                        case '@': {
                            bonuses.add(new Bonus(new Point(j, i), "big", 50));
                            break;
                        }
                        case '*': {
                            ghosts.add(new Ghost(new Point(j, i),
                                    colors[ghosts.size()],
                                    ghosts.size() % 2 == 0 ? new FollowPacmanStrategy() : new RandomStrategy()));
//                            if (ghosts.size() > 1) {
//                                map[i][j] = -1;
//                            }
                            break;
                        }
                        case '$': {
                            pacman = new Pacman(new Point(j, i));
                            break;
                        }
                        case 'L': {
                            exitLeftPos = new Point(j, i);
                            break;
                        }
                        case 'R': {
                            exitRightPos = new Point(j, i);
                            break;
                        }
//                        case ' ': {
//                            map[i][j] = 1;
//                            break;
//                        }
//                        case '%': {
//                            map[i][j] = -1;
//                            break;
//                        }
//                        case '^': {
//                            map[i][j] = -2;
//                            break;
//                        }
                        default: {

                        }
                    }
                }
            }

            bonuses.add(new Bonus(this.getRandomPos(), "fruit", 100));
            System.out.println("lalala");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("cannot read file");
        }
    }

    /**
     * Gain scores.
     */
    private void addScore(int scores) {
        this.score += scores;
    }

    /**
     * Update the game and wrap up the response.
     */
    public UpdateResponse update() {
        ++timer;
        updatePositions();
        updateStatus();
        return getResponse();
    }

    /**
     * Get random reachable loc.
     */
    public Point getRandomPos() {
        Random rand = new Random();
        Integer i = rand.nextInt(bonuses.size());
        return bonuses.get(i).getPos();
    }

    /**
     * Generate Response and return.
     *
     * @return information needed by frontend
     */
    public UpdateResponse getResponse() {
        UpdateResponse response = UpdateResponse.builder()
                .event(event)
                .difficulty(difficulty)
                .life(life)
                .score(score)
                .map(map)
                .bonuses(bonuses.toArray(new Bonus[bonuses.size()]))
                .ghosts(ghosts.toArray(new Ghost[ghosts.size()]))
                .pacman(pacman)
                .build();
        event = "";
        return response;
    }

    /**
     * Update all the positions of the objects.
     */
    private void updatePositions() {
        pacman.updatePos(timer);
        for (Ghost g : ghosts) {
            g.updatePos(timer);
        }
        for (Bonus b : bonuses) {
            b.updatePos(timer);
        }
    }

    /**
     * Update the status of all the objects. Used after updating positions.
     */
    private void updateStatus() {
        for (Ghost g : ghosts) {
            if (pacman.samePosWith(g)) {
                if (g.getStatus() == "normal") {
                    loseLife();
                    break;
                } else if (g.getStatus() == "flash") {
                    GoHomeStrategy goHome = new GoHomeStrategy(g.getStrategy());
                    g.setStatus("eyes");
                    g.setStrategy(goHome);
                    addScore(Ghost.getScore());
                    Ghost.setScore(Ghost.getScore() * 2);
                } else {
                    // eyes, ignore
                    continue;
                }
            }
        }

        for (Bonus b : bonuses) {
            if (pacman.samePosWith(b) && b.isVisible()) {
                b.disappear();
                addScore(b.getScore());
                if (b.getType() == "big") {
                    for (Ghost g : ghosts) {
                        g.setStatus("flash");
                    }
                }
                if (b.getType() == "fruit") {
                    b.disappear();
                }
            }
        }

        for (Bonus b : bonuses) {
            if (b.isVisible()) {
                return;
            }
        }
        // Has eaten all bonuses
        levelUp();
    }

    /**
     * Get the left exit.
     */
    public Point getExitLeftPos() {
        return exitLeftPos;
    }

    /**
     * Get the right exit.
     */
    public Point getExitRightPos() {
        return exitRightPos;
    }

    /**
     * Tell if a position is wall.
     */
    public Boolean isWall(Point p) {
        if (isValidPos(p) == false) {
            System.out.println("Error: invalid position: " + p.toString());
        }
        return map[p.y][p.x] == wall || map[p.y][p.x] == wall1;
    }

    /**
     * Find the shortest path from position src to position dst.
     *
     * @return a valid and correct direction to take.
     */
    public String findPath(Point src, Point dst) {
        // sanity check
        if (src == null || !isValidPos(src)) {
            System.out.println("Error: invalid src position " + src.toString());
        }
        if (dst == null || !isValidPos(dst)) {
            System.out.println("Error: invalid dst position " + dst.toString());
        }
        // check if we already know how to go from src to dst
        if (!pathDict.containsKey(src)) {
            pathDict.put(src, new HashMap<>());
        }
        if (pathDict.get(src).containsKey(dst)) {
            return pathDict.get(src).get(dst);
        }
        // find the path
        Queue<Point> q = new LinkedList<>();
        HashSet<Point> visited = new HashSet<>();
        HashMap<Point, String> tempDict = new HashMap<>();
        int x;
        int y;
        x = src.x - 1;
        y = src.y;
        if (x >= 0 && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
            visited.add(new Point(x, y));
            q.add(new Point(x, y));
            tempDict.put(new Point(x, y), "left");
        }

        x = src.x;
        y = src.y - 1;
        if (y >= 0 && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
            visited.add(new Point(x, y));
            q.add(new Point(x, y));
            tempDict.put(new Point(x, y), "up");
        }

        x = src.x + 1;
        y = src.y;
        if (x < this.map[0].length && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
            visited.add(new Point(x, y));
            q.add(new Point(x, y));
            tempDict.put(new Point(x, y), "right");
        }

        x = src.x;
        y = src.y + 1;
        if (y < this.map.length && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
            visited.add(new Point(x, y));
            q.add(new Point(x, y));
            tempDict.put(new Point(x, y), "down");
        }
        String dir = getPathLength(tempDict, visited, q, dst);
        pathDict.get(src).put(dst, dir);
        return dir;
    }

    /**
     * Find the shortest path from position src to position dst.
     * Assume dst is valid but does not assume src is valid.
     * Assume both src and dst are not null.
     *
     * @return the length.
     */
    private String getPathLength(HashMap<Point, String> tempDict, HashSet<Point> visited, Queue<Point> q, Point dst) {
        int x;
        int y;
        while (q.isEmpty() == false) {
            Point p = q.remove();

            if (p == dst) {
                return tempDict.get(dst);
            }

            x = p.x - 1;
            y = p.y;
            if (x >= 0 && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
                visited.add(new Point(x, y));
                q.add(new Point(x, y));
                tempDict.put(new Point(x, y), tempDict.get(p));
            }

            x = p.x;
            y = p.y - 1;
            if (y >= 0 && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
                visited.add(new Point(x, y));
                q.add(new Point(x, y));
                tempDict.put(new Point(x, y), tempDict.get(p));
            }

            x = p.x + 1;
            y = p.y;
            if (x < this.map[0].length && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
                visited.add(new Point(x, y));
                q.add(new Point(x, y));
                tempDict.put(new Point(x, y), tempDict.get(p));
            }

            x = p.x;
            y = p.y + 1;
            if (y < this.map.length && !visited.contains(new Point(x, y)) && !isWall(new Point(x, y))) {
                visited.add(new Point(x, y));
                q.add(new Point(x, y));
                tempDict.put(new Point(x, y), tempDict.get(p));
            }
        }
        return tempDict.get(dst);
    }

    /**
     * Check whether the position is inside the map.
     * @param p location
     * @return whether the position is inside the map
     */
    public Boolean isValidPos(Point p) {
        int width = map[0].length;
        int height = map.length;
        if (p.x < 0 || p.x >= width || p.y < 0 || p.y >= height) {
            return false;
        }
        return true;
    }

    /**
     * Get the position of pacman.
     *
     * @return the current position of the pacman
     */
    public Point getPacmanPos() {
        return pacman.getPos();
    }

    /**
     * Get the pacman.
     *
     * @return pacman object
     */
    public Pacman getPacman() {
        return pacman;
    }

    /**
     * Reset the game.
     */
    public static void reset() {
        ONLY = null;
    }

    /**
     * Get the life.
     */
    public Integer getLife() {
        return life;
    }

    /**
     * Get the difficulty.
     */
    public Integer getDifficulty() {
        return difficulty;
    }
}
