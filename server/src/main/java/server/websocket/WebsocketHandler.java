package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebsocketHandler {
    private final WebsocketSession connection = new WebsocketSession();
    private final UserService userService;
    private final GameService gameService;


    public WebsocketHandler(UserService userService, GameService gameService) {
        this.userService = userService;
        this.gameService = gameService;

    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            switch (userGameCommand.getCommandType()) {
                case CONNECT -> joingame(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                case MAKE_MOVE -> move(userGameCommand.getAuthToken(), userGameCommand.getGameID(), userGameCommand.getMove(), session);
                case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void joingame(String auth, Integer gameID, Session session) throws IOException, DataAccessException {
        String user = checkAuth(auth);
        if (Objects.equals(user, "EMPTY")){
            connection.add(gameID,user, session);
            error(user, "Error: auth failed", gameID);
            connection.remove(user, gameID);
            return;
        }
        connection.add(gameID,user,session);
        GameData game = checkGame(user,gameID);
        if (game == null){
            return;
        }

        String message;
        String color = getColor(game, user);

        if (color.isEmpty()) {
            message = String.format("%s joined %s as an observer", user, gameService.getGame(gameID).gameName());
        } else {
            message = String.format("%s joined %s as %s\n >>>", user, gameService.getGame(gameID).gameName(), color);
        }
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        notify(user, notification, gameID);

        loadGame(user, gameID, false);
    }

    private void move(String auth, Integer gameID, ChessMove move, Session session) throws IOException{
    }
    private void leave(String auth, Integer gameID, Session session) throws IOException, DataAccessException {
        String user = checkAuth(auth);
        if (Objects.equals(user, "badAuth")) {
            connection.newSend(session , new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: failed to find auth"));
            return;
        }
        GameData game = checkGame(user,gameID);
        if (Objects.equals(game, null)) {
            return;
        }

        if(getColor(game, user)!= null && game.status() != GameData.Status.ENDED) {
            if (ended(user, game)) {
                return;
            } else {
                gameService.leave(user, gameID);
            }
        }
        String message = String.format("%s left the game.", user);
        Notification notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        notify(user, notification, gameID);
        connection.remove(user, gameID);

    }

    private void resign(String auth, Integer gameID, Session session) throws IOException {
    }

    private String getColor(GameData game, String user){
        String teamColor = null;
        if(Objects.equals(game.blackUsername(), user)) {
            teamColor = "BLACK";
        }
        if(Objects.equals(game.whiteUsername(), user)) {
            teamColor = "WHITE";
        }
        return teamColor;
    }

    private String checkAuth(String auth){
        String user;
        try {
            user = userService.getAuth(auth).username();
        }
        catch (Exception e){
            user = "EMPTY";
        }
        return user;
    }

    private void error(String username, String message, Integer gameID) throws IOException {
        connection.send(username, new ErrorMessage(ServerMessage.ServerMessageType.ERROR, message), gameID);
    }

    private GameData checkGame(String user, Integer gameID) throws IOException {
        GameData game = null;
        try {
            game = gameService.getGame(gameID);
        }
        catch (Exception e) {
            error(user, "Error: Invalid game ID", gameID);
        }
        return game;
    }

    private void notify(String user, Notification notification, Integer gameID) throws IOException {
        connection.broadcast(user,notification,gameID);
    }

    public void loadGame(String username, Integer gameID, Boolean bool) throws IOException, DataAccessException {
        var load = new LoadGame(ServerMessage.ServerMessageType.LOAD_GAME, null, gameService.getGame(gameID));
        if (bool) {
            connection.broadcast(username, load, gameID);
        } else {
            connection.send(username, load, gameID);
        }
    }

    public Boolean ended(String username, GameData gameData) throws IOException {
        if (gameData.status()== GameData.Status.ENDED){
            error(username, "The game has ended.", gameData.gameID());
            return true;
        }
        else {
            return false;
        }
    }
}