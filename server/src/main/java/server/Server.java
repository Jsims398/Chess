package server;

import spark.*;
import dataaccess.*;
import service.GameService;
import service.UserService;


public class Server {
    //DAO's
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    //Service's
    UserService userService;
    GameService gameService;
    //Handeler's
    UserHandler userHandler;
    GameHandler gameHandler;

    public Server() {

        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        userService = new UserService(userDAO, authDAO);
        gameService = new GameService(gameDAO, authDAO);

        userHandler = new UserHandler(userService);
        gameHandler = new GameHandler(gameService);

    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
