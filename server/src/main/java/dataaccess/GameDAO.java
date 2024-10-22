package dataaccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
//    The information about the state of a game. This includes the players, the board,
//    and the current state of the game.
    HashSet<GameData> listGames();

    void createGame(GameData game);

    GameData getGame(int gameID) throws DataAccessException;

    boolean gameExists(int gameID);

    void updateGame(GameData game);

    void clear();
}
