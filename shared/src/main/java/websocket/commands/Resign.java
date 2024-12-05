package websocket.commands;

public class Resign extends UserGameCommand {
    public Resign(CommandType type, String auth, Integer gameID) {
        super(type, auth, gameID);
    }
}