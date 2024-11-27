package websocket;


import com.google.gson.Gson;
import facade.ResponseException;
import websocket.messages.Message;

import javax.websocket.*;
import java.io.*;
import java.net.*;

public class WebsocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public void WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    Message text = new Gson().fromJson(message, Message.class);
                    var type = text.getServerMessageType();
                    switch (type){
                        case LOAD_GAME -> {
                            //fill out
                        }
                        case NOTIFICATION -> {
                            Message words = new Gson().fromJson(message, Message.class);
                            System.out.println(words.getMessageText());
                        }
                        case ERROR -> {
                            Error error = new Gson().fromJson(message, Error.class);
//                            System.out.println(error);
                        }
                    }
                }
            });
        }
        catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void joinGame(){

    }
    public void leaveGame(){

    }
    public void makeMove(){

    }
    public void resignGame(){

    }


}
