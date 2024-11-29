package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class WebsocketSession {

    public final ConcurrentHashMap<Integer, HashMap<String, Connection>> connections = new ConcurrentHashMap<>();

    public void add(Integer gameID, String username, Session session) {
        var connection = new Connection(username, session);
        if (!connections.containsKey(gameID)){
            connections.put(gameID, new HashMap<>());
        }
        connections.get(gameID).put(username, connection);
    }

    public void remove(String username, Integer gameID) {
        connections.get(gameID).remove(username);
    }


    public void broadcast(String excludedUser, ServerMessage message, Integer gameID) throws IOException {
        var removeList = new ArrayList<Connection>();
        for (var connection : connections.get(gameID).values()) {
            if (connection.session.isOpen()) {
                if (!connection.username.equals(excludedUser)) {
                    connection.send(new Gson().toJson(message));
                }
            } else {
                removeList.add(connection);
            }
        }

        for (var connection : removeList) {
            connections.remove(connection.username);
        }
    }

    public void send(String user, ServerMessage message, Integer gameID) throws IOException {
        var connection = connections.get(gameID).get(user);
        if (connection.session.isOpen()) {
            connection.send(new Gson().toJson(message));
        } else {
            connections.remove(connection.username);
            var msg = String.format("%s is not connected", user);
            System.out.println(msg);
        }
    }


    public void newSend(Session session, ServerMessage message) throws IOException {
        Connection connection = null;
        for (var c : connections.values()) {
            for (var con : c.values()){
                if (con.session == session) {
                    connection = con;
                }
            }
        }
        if (connection == null) {
            var msg = String.format("Not connected");
            System.out.println(msg);
            return;
        }
        if (connection.session.isOpen()) {
            connection.send(new Gson().toJson(message));
        } else {
            connections.remove(connection.username);
            var msg = String.format("Not connected");
            System.out.println(msg);
        }
    }

}