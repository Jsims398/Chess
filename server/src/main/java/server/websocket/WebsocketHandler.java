package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
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

        if (color == null) {
            message = String.format("%s joined %s as an observer", user, gameService.getGame(gameID).gameName());
            loadGame(user, gameID, true);
        }
        else {
            message = String.format("%s joined %s as %s\n", user, gameService.getGame(gameID).gameName(), color);
        }
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        notify(user, notification, gameID);

        loadGame(user, gameID, false);
    }

    private void move(String auth, Integer gameID, ChessMove move, Session session) throws IOException, DataAccessException {
        var username = checkAuth(auth);
        if (Objects.equals(username, "badAuth")) {
            connection.newSend(session , new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Error: Bad auth")); return;}

        GameData gameData = checkGame(username, gameID);
        boolean value = check(gameData, username);
        if(!value){
            return;
        }

        ChessGame newGame = gameData.game();
        try {
            newGame.makeMove(move);
        }
        catch (InvalidMoveException e) {
            error(username, String.format("Invalid move. %s", e.getMessage() ), gameID);
            return;
        }
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(getColor(gameData, username));
        ChessGame.TeamColor check = newGame.getBoard().getPiece(move.getEndPosition()).getTeamColor();
        if (teamColor != check) {
            error(username, "Cannot move opponent's pieces", gameID);
            return;
        }

        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), newGame, GameData.Status.PLAYING);
        try {
            gameService.updateGame(newGameData);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        loadGame(null, gameID, true);

        notify(username, new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                String.format("%s moved %s to %s", username, move.getStartPosition(), move.getEndPosition())), gameID);

        checksOrMatesCheck(newGame, newGameData);

    }

    private boolean observer(String username, GameData gameData) throws IOException {
        ChessGame.TeamColor teamColor = ChessGame.TeamColor.valueOf(getColor(gameData, username));
        if (teamColor == null) {
            error(username, "Not a player. Cannot make moves.", gameData.gameID());
            return true;
        }
        return false;
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

    private Boolean check(GameData gameData, String username) throws IOException {
        if (gameData == null){
            return false;
        }
        if(observer(username, gameData)) {
            return false;
        }
        return !ended(username, gameData);
    }

    private void resign(String auth, Integer gameID, Session session) throws IOException {
        var username = checkAuth(auth);
        if (Objects.equals(username, "badAuth")) {connection.newSend(session ,
                new ErrorMessage(ServerMessage.ServerMessageType.ERROR,"Bad auth")); return;}

        GameData gameData = checkGame(username, gameID);

        boolean value = check(gameData, username);

        if(!value){
            return;
        }

        ChessGame newGame = gameData.game();
        GameData newGameData = new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(),
                gameData.gameName(), newGame, GameData.Status.ENDED);
        try {
            gameService.updateGame(newGameData);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        var message = String.format("%s resigned the game. Game over.", username);
        var notification = new Notification(ServerMessage.ServerMessageType.NOTIFICATION, message);
        notify(null, notification, gameID);
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
            error(user, "Invalid game ID", gameID);
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

    public void checksOrMatesCheck(ChessGame game, GameData gameData) throws IOException {
        String checked = null;
        if (game.isInCheck(ChessGame.TeamColor.BLACK)){
            checked = gameData.blackUsername();
        }
        else if (game.isInStalemate(ChessGame.TeamColor.WHITE)) {
            checked = gameData.whiteUsername();
        }

        if (checked != null) {
            notify(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s is in check!", checked)), gameData.gameID());
        }

        String trapped = null;
        String attacker = null;

        if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
            trapped = gameData.blackUsername();
            attacker = gameData.whiteUsername();
        }
        else if (game.isInCheckmate(ChessGame.TeamColor.WHITE)){
            attacker = gameData.blackUsername();
            trapped = gameData.whiteUsername();
        }

        if (trapped != null) {
            notify(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s is in checkmate. %s wins!", trapped, attacker)), gameData.gameID());
            endGame(gameData);
        }

        String stuck = null;
        String free = null;

        if(game.isInStalemate(ChessGame.TeamColor.BLACK)){
            stuck = gameData.blackUsername();
            free = gameData.whiteUsername();
        } else if (game.isInStalemate(ChessGame.TeamColor.WHITE)){
            stuck = gameData.whiteUsername();
            free = gameData.blackUsername();
        }

        if(stuck != null){
            notify(null, new Notification(ServerMessage.ServerMessageType.NOTIFICATION,
                    String.format("%s is in stalemate. %s wins!", stuck, free)), gameData.gameID());
            endGame(gameData);
        }
    }

    public void endGame(GameData newGameData){
        try{ gameService.updateGame(new GameData(newGameData.gameID(),
                newGameData.whiteUsername(), newGameData.blackUsername(), newGameData.gameName(),
                newGameData.game(), GameData.Status.ENDED));} catch (Exception e) { System.out.println(e.getMessage());}
    }

}
