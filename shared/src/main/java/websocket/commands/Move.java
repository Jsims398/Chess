package websocket.commands;

import chess.ChessMove;
import model.AuthData;

public class Move extends UserGameCommand {
    public Move(CommandType commandType, AuthData authToken, Integer gameID, ChessMove move) {
        super(commandType, String.valueOf(authToken.authToken()), gameID);
        super.move = move;
    }

}