package websocket.commands;

public class Resign extends UserGameCommand {
    public Resign(CommandType Type, String auth, Integer gameID) {
        super(Type, auth, gameID);
    }
}