package edu.rice.comp504.controller;

import com.google.gson.Gson;
import edu.rice.comp504.model.DispatchAdapter;

import static spark.Spark.*;


/**
 * Line draw controller is responsible for interfacing between the model and view for drawing a moving line.
 */
public class PacmanController {

    /**
     * Main entry point into the program.
     *
     * @param args Arguments that are normally passed to the executable on the command line
     */
    public static void main(String[] args) {
        staticFileLocation("/public");
        port(getHerokuAssignedPort());
        Gson gson = new Gson();
        DispatchAdapter da = new DispatchAdapter();


        get("/init", (req, res) -> {
            return gson.toJson(da.initGame().getResponse());
        });

        get("/update", (req, res) -> {
            return gson.toJson(da.updateGame());
        });

        post("/control/:direction", (req, res) -> {
            String direction = req.params(":direction");
            da.userControl(direction);
            return "";
        });
    }

    /**
     * Get the Heroku assigned port number.
     *
     * @return Heroku assigned port number
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
