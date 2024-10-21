package server;

import dataaccess.*;
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
    //listgames
    public Object listGames(Request req, Response resp) throws UnauthorizedException {
        String authToken = req.headers("authorization");
        HashSet<GameData> games = gameService.listGames(authToken);
        resp.status(200);
        return "{games: %s}".formatted(new Gson().toJson(games));
    }
    //creategames
    public Object createGame(Request req, Response resp) throws BadRequestException, UnauthorizedException {

        if (!req.body().contains("gameName:")) {
            throw new BadRequestException("No gameName provided");
        }

        String authToken = req.headers("authorization");
        int gameID =  gameService.createGame(authToken);

        resp.status(200);
        return "{gameID: %d }".formatted(gameID);
    }
    //joingame
    public Object joinGame(Request request, Response response) throws BadRequestException, UnauthorizedException {
        //base case
        if (!request.body().contains("gameID:")) {
            throw new BadRequestException("No gameID provided");
        }
        //authentication
        String auth = request.headers("authorization");
        record JoinGameData(String playerColor, int gameID) {
        }

        JoinGameData data = new Gson().fromJson(request.body(), JoinGameData.class);
        boolean success = gameService.joinGame(auth, data.gameID(), data.playerColor());

        if (success == false) {
            response.status(403);
            return "{message: Error: already taken}";
        }

        response.status(200);
        return "{}";
    }
}
