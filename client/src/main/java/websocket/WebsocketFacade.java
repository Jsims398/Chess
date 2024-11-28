package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import facade.ResponseException;
import model.AuthData;
import model.GameData;
import websocket.commands.Connect;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
public class WebsocketFacade extends Endpoint {
    Session session;
    Integer gameID;
    String teamColor;
    NotificationHandler notificationHandler;
    GameData gameData;

    public WebsocketFacade(String url, NotificationHandler handler, Integer gameID, String teamColor) throws ResponseException {

        this.notificationHandler = handler;
        this.gameID = gameID;
        this.teamColor = teamColor;

        try{
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new javax.websocket.MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    var gson = new Gson();
                    ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
                    switch (serverMessage.getServerMessageType()) {
                        case NOTIFICATION -> handler.notify(gson.fromJson(message, Notification.class));
                        case LOAD_GAME -> loadGame(gson.fromJson(message, LoadGame.class).getGame());
                        case ERROR -> handler.notifyError(gson.fromJson(message, ErrorMessage.class));
                    }

                }
            });

        } catch (Exception e ){
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void connect(String authToken) throws ResponseException {
        try {
            var command = new Connect(UserGameCommand.CommandType.CONNECT, authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException e) {
            throw new ResponseException(500, e.getMessage());
        }

    }

//    public void resignGame(String authToken) throws ResponseException {
//        try {
//            var command = new Resign(UserGameCommand.CommandType.RESIGN, authToken, gameID);
//            this.session.getBasicRemote().sendText(new Gson().toJson(command));
//        } catch (IOException e) {
//            throw new ResponseException(500, e.getMessage());
//        }
//    }

    public boolean joinGame(int gameId, String color, AuthData auth) {
        try {
            String message = String.format("{\"action\":\"joinGame\",\"gameId\":%d,\"color\":\"%s\",\"authToken\":\"%s\"}",
                    gameId, color, auth.authToken());
            session.getBasicRemote().sendText(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public boolean makeMove(ChessMove move, AuthData auth) throws ResponseException {
//        try {
//            var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, auth, gameID, move);
//            this.session.getBasicRemote().sendText(new Gson().toJson(command));
//        } catch (IOException e) {
//            throw new ResponseException(500, e.getMessage());
//        }
//    }

    public void printboard(AuthData auth) {
        loadGame(gameData);
    }
//
    public void loadGame(GameData game) {
        this.gameData = game;
        notificationHandler.updateGame(game);
//        board = new DrawBoard(game, teamColor);
        System.out.println();
//        PrintBoard.draw(null, null);
    }
//
//    public void connect(AuthData auth) {
//    }
}
