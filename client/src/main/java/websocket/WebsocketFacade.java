package websocket;

import com.google.gson.Gson;
import facade.ResponseException;
import model.AuthData;
import websocket.messages.Notification;
import javax.websocket.*;
import java.net.URI;

public class WebsocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebsocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
    try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {

                @Override
                public void onMessage(String message) {
                    Notification notification = new Gson().fromJson(message, Notification.class);
                    notificationHandler.notify(notification);
                }
            }
            );
        }
        catch (Exception e) {
            throw new ResponseException(500, e.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public boolean resignGame(AuthData auth) {
        try {
            String message = String.format("{\"action\":\"resign\",\"authToken\":\"%s\"}", auth.authToken());
            session.getBasicRemote().sendText(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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

    public boolean makeMove(String from, String to, AuthData auth) {
        try {
            String message = String.format("{\"action\":\"move\",\"from\":\"%s\",\"to\":\"%s\",\"authToken\":\"%s\"}",
                    from, to, auth.authToken());
            session.getBasicRemote().sendText(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void printboard(AuthData auth) {
        try {
            String message = String.format("{\"action\":\"printboard\",\"authToken\":\"%s\"}", auth.authToken());
            session.getBasicRemote().sendText(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
