package passoff;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import service.UserService;
import service.GameService;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOMemory();
        authDAO = new AuthDAOMemory();
        userService = new UserService(userDAO, authDAO);
    }

    @AfterEach
    void tearDown() {
        userDAO.clear();
        authDAO.clear();
    }

    @Test
    void createUser_success() throws BadRequestException {
        // Arrange
        UserData userData = new UserData("testUser", "password", "email@test.com");

        // Act
        AuthData result = userService.createUser(userData);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void createUser_failure() {
        // Arrange
        UserData userData = new UserData(null, "password", "email@test.com");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userService.createUser(userData));
    }

    @Test
    void loginUser_success() throws UnauthorizedException, BadRequestException {
        // Arrange
        UserData userData = new UserData("testUser", "password", "email@test.com");
        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            fail("Test setup failed");
        }

        // Act
        AuthData result = userService.loginUser(userData);

        // Assert
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginUser_failure() {
        // Arrange
        UserData userData = new UserData("nonexistentUser", "wrongPassword", "email@test.com");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(userData));
    }

    @Test
    void logoutUser_success() throws DataAccessException, BadRequestException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("testUser", authToken));

        // Act & Assert
        assertDoesNotThrow(() -> userService.logoutUser(authToken));
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void logoutUser_failure() {
        // Arrange
        String invalidAuthToken = "invalidToken";

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser(invalidAuthToken));
    }

    @Test
    void clear_success() {
        // Arrange
        try {
            userDAO.createUser(new UserData("testUser", "password", "email@test.com"));
            authDAO.addAuth(new AuthData("testUser", "token"));
        } catch (DataAccessException | BadRequestException e) {
            fail("Test setup failed");
        }

        // Act
        userService.clear();

        // Assert
        assertTrue(true); // If no exception is thrown, the test passes
    }
}

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
    void listGames_success() throws UnauthorizedException, BadRequestException {
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
    void listGames_failure() {
        // Arrange
        String invalidAuthToken = "invalidToken";

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(invalidAuthToken));
    }

    @Test
    void createGame_success() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        String gameName = "TestGame";

        // Act
        int gameId = gameService.createGame(authToken, gameName);

        // Assert
        assertTrue(gameId > 0);
        GameData game = gameDAO.getGame(gameId);
        assertNotNull(game);
        assertEquals(gameName, game.gameName());
    }

    @Test
    void createGame_failure() {
        // Arrange
        String invalidAuthToken = "invalidToken";
        String gameName = "TestGame";

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(invalidAuthToken, gameName));
    }

    @Test
    void joinGame_success() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        int gameId = gameService.createGame(authToken, "TestGame");

        // Act
        boolean result = gameService.joinGame(authToken, gameId, "WHITE");

        // Assert
        assertTrue(result);
        GameData game = gameDAO.getGame(gameId);
        assertEquals("user1", game.whiteUsername());
    }

    @Test
    void joinGame_failure() throws UnauthorizedException, BadRequestException {
        // Arrange
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        int gameId = gameService.createGame(authToken, "TestGame");

        // Act & Assert
        assertThrows(BadRequestException.class, () -> gameService.joinGame(authToken, gameId, "INVALID_COLOR"));
    }

    @Test
    void clear_success() throws BadRequestException {
        // Arrange
        GameData game = new GameData(1, null, null, "TestGame", null);
        gameDAO.createGame(game);
        authDAO.addAuth(new AuthData("user1", "token"));

        // Act
        gameService.clear();

        // Assert
        assertTrue(gameDAO.listGames().isEmpty());
    }
}