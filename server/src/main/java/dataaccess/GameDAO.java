package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashSet;

public interface GameDAO {
//    The information about the state of a game. This includes the players, the board,
//    and the current state of the game.
    HashSet<GameData> listGames() throws DataAccessException, SQLException;

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    boolean gameExists(int gameID) throws DataAccessException;

    void updateGame(GameData game) throws DataAccessException;

    void clear() throws DataAccessException;
}
