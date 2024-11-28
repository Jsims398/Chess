package websocket.messages;

import model.GameData;

public class LoadGame extends ServerMessage {
    GameData game;

    public LoadGame(ServerMessageType type, String message, GameData game) {
        super(type);
        super.message = message;
        this.game = game;
    }

    public GameData getGame() {
        return game;
    }
}