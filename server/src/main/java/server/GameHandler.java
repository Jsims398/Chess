package server;

import dataaccess.UnauthorizedException;
import service.GameService;
import spark.*;
import model.GameData;
import com.google.gson.Gson;

import java.util.HashSet;

public class GameHandler {

    GameService gameService;
    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Request req, Response resp) throws UnauthorizedException {
        String authToken = req.headers("authorization");
        HashSet<GameData> games = gameService.listGames(authToken);
        resp.status(200);
        return "{ \"games\": %s}".formatted(new Gson().toJson(games));
    }

}
