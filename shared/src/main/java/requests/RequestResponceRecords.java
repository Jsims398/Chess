package requests;

import java.util.List;

public class RequestResponseRecords {

    public record ClearRequest() {}
    public record ClearResult(boolean success, String message) {}

    public record CreateGameRequest(String authToken, String gameName) {}
    public record CreateGameResult(String gameId, String gameName) {}

    public record JoinGameRequest(String authToken, String playerColor, int gameID) {}
    public record JoinGameResult(boolean success, String message) {}

    public record ListGamesRequest(String authToken) {}
    public record ListGamesResult(List<String> games) {}

    public record LoginRequest(String username, String password) {}
    public record LoginResult(String authToken, String username) {}

    public record LogoutRequest(String authToken) {}
    public record LogoutResult(boolean success, String message) {}

    public record RegisterRequest(String username, String password, String email) {}
    public record RegisterResult(String userId, String username) {}
}
