package facade;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import model.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public void clear() throws ResponseException {
        var path = "/clear";
        this.makeRequest("POST", path, null, null, null);
    }

    public UserData register(UserData user) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, user, UserData.class, null);
    }

    public AuthData login(UserData user) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, user, AuthData.class, null);
    }

    public void logout(AuthData auth) throws ResponseException {
        var path = "/session";
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("DELETE");
            http.setDoOutput(true);
            http.setRequestProperty("authorization", auth.authToken());
            writeBody(auth, http);
            http.connect();
            throwIfNotSuccessful(http);
            readBody(http, AuthData.class);
        } catch (Exception exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    public GameData[] listGames(AuthData auth) throws ResponseException {
        var path = "/game";
        record listPetResponse(GameData[] games) {}
        var response = this.makeRequest("GET", path, null, listPetResponse.class, auth);
        return response.games();
    }



    public void createGame(String game, AuthData auth) throws ResponseException {
        var path = "/game";
        JsonObject gameRequest = new JsonObject();
        gameRequest.addProperty("gameName", game);
        this.makeRequest("POST", path, gameRequest, GameData.class, auth);
    }


    public GameData joinGame(int gameId, String playerColor, AuthData auth) throws ResponseException {
        var path = "/game";
        JsonObject gameRequest = new JsonObject();
        gameRequest.addProperty("playerColor", playerColor);
        gameRequest.addProperty("gameID", gameId);

        return this.makeRequest("PUT", path, gameRequest, GameData.class, auth);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, AuthData auth) throws ResponseException {
        try {
            URL url = new URI(serverUrl + path).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (auth != null) {
                addAuthorizationHeader(http, auth);
            }
            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception exception) {
            throw new ResponseException(500, exception.getMessage());
        }
    }

    private void addAuthorizationHeader(HttpURLConnection http, AuthData auth) {
        if (auth != null && auth.authToken() != null) {
            http.setRequestProperty("Authorization", auth.authToken());
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
