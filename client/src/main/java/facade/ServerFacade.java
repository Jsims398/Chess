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
        makeRequest("POST", path, null, null);
    }

    public UserData register(UserData user) throws ResponseException {
        var path = "/register";
        return makeRequest("POST", path, user, UserData.class);
    }

    public UserData login(UserData user) throws ResponseException {
        var path = "/login";
        return makeRequest("POST", path, user, UserData.class);
    }

    public void logout(int userId) throws ResponseException {
        var path = String.format("/logout/%d", userId);
        makeRequest("POST", path, null, null);
    }

    public GameData[] listGames() throws ResponseException {
        var path = "/games";
        record ListGamesResponse(GameData[] games) {}
        var response = makeRequest("GET", path, null, ListGamesResponse.class);
        return response.games();
    }

    public GameData createGame(GameData game) throws ResponseException {
        var path = "/game";
        return makeRequest("POST", path, game, GameData.class);
    }

    public GameData joinGame(int gameId, GameData user) throws ResponseException {
        var path = String.format("/game/%d/join", gameId);
        return makeRequest("POST", path, user, GameData.class);
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
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
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
            throw new ResponseException(status, "Request failed with status: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() > 0) {
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
    public boolean isConnected() {
        try {
            var path = "/status";
            HttpURLConnection http = (HttpURLConnection) new URI(serverUrl + path).toURL().openConnection();
            http.setRequestMethod("GET");
            http.connect();
            int status = http.getResponseCode();
            return isSuccessful(status);
        } catch (Exception ex) {
            return false;
        }
    }

}
