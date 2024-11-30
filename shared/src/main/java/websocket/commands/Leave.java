package websocket.commands;

import model.AuthData;

public class Leave extends UserGameCommand {
    public Leave(UserGameCommand.CommandType commandType, AuthData auth, Integer gameID) {
        super(commandType, String.valueOf(auth.authToken()), gameID);
    }
}
