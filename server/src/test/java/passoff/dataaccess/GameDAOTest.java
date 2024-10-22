package passoff.dataaccess;

import model.GameData;
import dataaccess.DataAccessException;
import dataaccess.GameDAOMemory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GameDAOTest {
    private GameDAOMemory gameDAO;
    private GameData testGame;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAOMemory();
        gameDAO.clear();
        testGame = new GameData(1, null, null, "TestGame", null);
    }

    @AfterEach
    void tearDown() {
        gameDAO.clear();
    }

    @Test
    void listGames_empty() {
        var games = gameDAO.listGames();
        assertNotNull(games);
        assertTrue(games.isEmpty());
    }

    @Test
    void listGames_multiple() {
        gameDAO.createGame(testGame);
        GameData game2 = new GameData(2, null, null, "TestGame2", null);
        gameDAO.createGame(game2);

        var games = gameDAO.listGames();
        assertEquals(2, games.size());
        assertTrue(games.contains(testGame));
        assertTrue(games.contains(game2));
    }

    @Test
    void createGame_success() {
        assertDoesNotThrow(() -> {
            gameDAO.createGame(testGame);
            GameData retrieved = gameDAO.getGame(testGame.gameID());
            assertEquals(testGame, retrieved);
        });
    }

    @Test
    void getGame_success() throws DataAccessException {
        gameDAO.createGame(testGame);
        GameData retrieved = gameDAO.getGame(testGame.gameID());
        assertEquals(testGame, retrieved);
    }

    @Test
    void getGame_nonexistent() {
        assertThrows(DataAccessException.class, () ->
                gameDAO.getGame(999));
    }

    @Test
    void gameExists_true() {
        gameDAO.createGame(testGame);
        assertTrue(gameDAO.gameExists(testGame.gameID()));
    }

    @Test
    void gameExists_false() {
        assertFalse(gameDAO.gameExists(999));
    }

    @Test
    void updateGame_existingGame() {
        gameDAO.createGame(testGame);
        GameData updatedGame = new GameData(
                testGame.gameID(),
                "white",
                null,
                testGame.gameName(),
                null
        );

        gameDAO.updateGame(updatedGame);

        assertDoesNotThrow(() -> {
            GameData retrieved = gameDAO.getGame(testGame.gameID());
            assertEquals("white", retrieved.whiteUsername());
        });
    }

    @Test
    void updateGame_nonexistentGame() {
        GameData newGame = new GameData(999, null, null, "NewGame", null);
        assertDoesNotThrow(() -> gameDAO.updateGame(newGame));
        assertTrue(gameDAO.gameExists(999));
    }

    @Test
    void clear_success() {
        gameDAO.createGame(testGame);
        gameDAO.clear();
        assertTrue(gameDAO.listGames().isEmpty());
        assertThrows(DataAccessException.class, () ->
                gameDAO.getGame(testGame.gameID()));
    }
}