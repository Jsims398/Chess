package server;

import dataaccess.*;
import service.GameService;
import spark.*;
import model.GameData;
import com.google.gson.*;

import java.util.HashSet;
import java.util.Map;

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
        return new Gson().toJson(Map.of("games", games));
    }
    //creategames
    public Object createGame(Request request, Response response) throws BadRequestException, UnauthorizedException {
        System.out.println("Auth header: " + request.headers("authorization"));
        System.out.println("Request body: " + request.body());
        String authToken = request.headers("authorization");
        if (authToken == null) {
            response.status(401);
            throw new UnauthorizedException();
        }
        try {
            JsonObject gameRequest = new Gson().fromJson(request.body(), JsonObject.class);
            if (gameRequest == null || !gameRequest.has("gameName")) {
                throw new BadRequestException("Missing game name");
            }
            String gameName = gameRequest.get("gameName").getAsString();
            int gameID = gameService.createGame(authToken, gameName);
            response.status(200);
            return "{\"gameID\": " + gameID + "}";
        }
        catch (JsonSyntaxException exception) {
            response.status(400);
            return "{\"message\": \"Bad Request: Invalid JSON format\"}";
        }
        catch (BadRequestException exception) {
            response.status(400);
            return "{\"message\": \"Bad Request: " + exception.getMessage() + "\"}";
        }
    }
    //joingame
    public Object joinGame(Request request, Response response) throws BadRequestException, UnauthorizedException, DataAccessException {
        //authentication
        String auth = request.headers("authorization");
        if (auth == null) {
            throw new UnauthorizedException();
        }
        record JoinGameData(String playerColor, int gameID) {
        }
        JoinGameData data = new Gson().fromJson(request.body(), JoinGameData.class);
        boolean success = gameService.joinGame(auth, data.gameID(), data.playerColor());
        if (!success) {
            response.status(403);
            return "{\"message\": \"Error: already taken\"}";
        }
        response.status(200);
        return "{}";
    }
}
