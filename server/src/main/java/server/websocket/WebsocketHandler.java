package server.websocket;

import chess.ChessMove;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import service.GameService;
import service.UserService;
import websocket.commands.UserGameCommand;

import java.io.IOException;

@WebSocket
public class WebsocketHandler {
    private final WebsocketSession connections = new WebsocketSession();
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
                case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                case MAKE_MOVE -> move(userGameCommand.getAuthToken(), userGameCommand.getGameID(), userGameCommand.getMove(), session);
                case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                case RESIGN -> resign(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect(String authToken, Integer gameID, Session session) throws IOException {
    }
    private void move(String authToken, Integer gameID, ChessMove move, Session session) throws IOException{
    }
    private void leave(String authToken, Integer gameID, Session session) throws IOException{
    }
    private void resign(String authToken, Integer gameID, Session session) throws IOException {
    }
}

