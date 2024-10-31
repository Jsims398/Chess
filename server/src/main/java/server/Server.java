package server;

import dataaccess.GameDAO;
import spark.*;
import dataaccess.*;
import service.GameService;
import service.UserService;


public class Server {
    //DAO's
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private GameDAO gameDAO;
    //Service's
    UserService userService;
    GameService gameService;
    //Handeler's
    UserHandler userHandler;
    GameHandler gameHandler;

    public Server() {
// get everything
        try {
            this.userDAO = new UserSQLDAO();
            this.authDAO = new AuthSQLDAO();
            this.gameDAO = new GameSQLDAO();
        }
        catch (DataAccessException exception){
            System.out.println("failed to start memory");
        }
        this.userService = new UserService(userDAO, authDAO);
        this.gameService = new GameService(gameDAO, authDAO);

        this.userHandler = new UserHandler(userService);
        this.gameHandler = new GameHandler(gameService);

    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.delete("/db", this::clear);
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        Spark.get("/game", gameHandler::listGames);
        Spark.post("/game", gameHandler::createGame);
        Spark.put("/game", gameHandler::joinGame);

        Spark.exception(BadRequestException.class, this::badRequestExceptionHandler);
        Spark.exception(UnauthorizedException.class, this::unauthorizedExceptionHandler);
        Spark.exception(Exception.class, this::genericExceptionHandler);

        Spark.awaitInitialization();
        return Spark.port();
    }
    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
    private Object clear(Request req, Response resp) {

        userService.clear();
        gameService.clear();

        resp.status(200);
        return "{}";
    }
    //handle exceptions
    private void badRequestExceptionHandler(BadRequestException ex, Request req, Response resp) {
        resp.status(400);
        resp.body("{\"message\": \"Error: bad request\"}");
    }

    private void unauthorizedExceptionHandler(UnauthorizedException ex, Request req, Response resp) {
        resp.status(401);
        resp.body("{\"message\": \"Error: unauthorized\"}");
    }

    private void genericExceptionHandler(Exception ex, Request req, Response resp) {
        resp.status(500);
        resp.body("{\"message\": \"Error: %s\"}".formatted(ex.getMessage()));
    }
}