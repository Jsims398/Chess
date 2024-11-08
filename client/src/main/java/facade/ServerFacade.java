package facade;

import com.google.gson.Gson;
import model.*;

import java.io.*;
import java.net.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/clear";
        this.makeRequest("POST", path, null, null);
    }

    public UserData register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, UserData.class);
    }

    public UserData login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, UserData.class);
    }

    public void logout(UserData user) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, user, null);
    }

    public GameData[] listGames() throws ResponseException {
        var path = "/games";
        record ListGamesResponse(GameData[] games) {}
        var response = this.makeRequest("GET", path, null, ListGamesResponse.class);
        return response.games();
    }

    public GameData createGame(GameData game) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, game, GameData.class);
    }

    public GameData joinGame(int gameId, GameData user) throws ResponseException {
        var path = String.format("/game/%d/join", gameId);
        return this.makeRequest("POST", path, user, GameData.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        }
        catch (Exception exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        int status = http.getResponseCode();
        if (!isSuccessful(status)) {
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
