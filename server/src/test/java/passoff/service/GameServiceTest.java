package passoff.service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.GameService;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAOMemory();
        authDAO = new AuthDAOMemory();
        gameService = new GameService(gameDAO, authDAO);
    }

    @AfterEach
    void tearDown() {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void listGames() throws UnauthorizedException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        GameData game1 = new GameData(1, null, null, "Game1", null);
        GameData game2 = new GameData(2, null, null, "Game2", null);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);

        // Act
        HashSet<GameData> actualGames = gameService.listGames(authToken);

        // Assert
        assertEquals(2, actualGames.size());
        assertTrue(actualGames.contains(game1));
        assertTrue(actualGames.contains(game2));
    }

    @Test
    void createGame() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken)); // Simulate an authenticated user
        String gameName = "Game1";

        // Act
        int gameID = gameService.createGame(authToken, gameName);

        // Assert
        assertTrue(gameID > 0); // Ensure a valid game ID is returned
        GameData createdGame = gameDAO.getGame(gameID);
        assertNotNull(createdGame);
        assertEquals(gameName, createdGame.gameName());
    }

    @Test
    void createGamewithUnauthorizedException() {
        // Arrange
        String authToken = null; // No authentication token
        String gameName = "Game1";

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(authToken, gameName));
    }

    @Test
    void createGamewithBadRequestException() {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken)); // Simulate an authenticated user
        String gameName = " "; // Invalid game name

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> gameService.createGame(authToken, gameName));
        assertEquals("Game name is empty", exception.getMessage());
    }

    @Test
    void joinGame() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken)); // Simulate an authenticated user
        int gameID = gameService.createGame(authToken, "Game1"); // Create a game

        // Act
        boolean result = gameService.joinGame(authToken, gameID, "WHITE");

        // Assert
        assertTrue(result); // Joining should succeed
        GameData gameData = gameDAO.getGame(gameID);
        assertEquals("user1", gameData.whiteUsername()); // Check if the user is assigned as white
    }

    @Test
    void joinGamewithColorAlreadyTaken() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken)); // Simulate an authenticated user
        int gameID = gameService.createGame(authToken, "Game1"); // Create a game
        gameService.joinGame(authToken, gameID, "WHITE"); // Join the game as white

        // Act
        boolean result = gameService.joinGame(authToken, gameID, "WHITE"); // Try to join again

        // Assert
        assertFalse(result); // Joining should fail since the color is already taken
    }

    @Test
    void joinGamewithBadRequestException() throws UnauthorizedException, BadRequestException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken)); // Simulate an authenticated user
        int gameID = gameService.createGame(authToken, "Game1"); // Create a game

        // Act & Assert
        BadRequestException exception = assertThrows(BadRequestException.class, () -> gameService.joinGame(authToken, gameID, "INVALID_COLOR"));
        assertEquals("INVALID_COLOR is not a valid team color", exception.getMessage());
    }

    @Test
    void clear() {
        // Act
        gameService.clear();
    }
}
