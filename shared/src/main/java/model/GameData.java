package model;

import chess.ChessGame;

public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game, Status status) {
    public enum Status{
        PLAYING,ENDED
    }
}
