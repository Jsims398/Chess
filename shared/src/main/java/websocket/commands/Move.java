package websocket.commands;

import chess.ChessMove;

public class Move extends UserGameCommand {
    public Move(CommandType commandType, String authToken, Integer gameID, ChessMove move) {
        super(commandType, authToken, gameID);
        super.move = move;
    }

}