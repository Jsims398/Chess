package websocket.messages;

import com.google.gson.Gson;

public record Notification(Type type, String message) {
    public enum Type {
        JOIN,
        UPDATE,
        LEAVE
    }

    public String toString() {
        return new Gson().toJson(this);
    }
}

