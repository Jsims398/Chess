package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setUp() {
        try {
            userDAO = new UserSQLDAO();
            authDAO = new AuthSQLDAO();
        }
        catch (DataAccessException e){
            System.out.println("couldnt start SQL");
        }
        userService = new UserService(userDAO, authDAO);
    }

    @AfterEach
    void tearDown() {
        try {
            userDAO.clear();
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        authDAO.clear();
    }

    @Test
    void createUsersuccess() throws BadRequestException {
        UserData userData = new UserData("testUser", "password", "email@test.com");
        AuthData result = userService.createUser(userData);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void createUserfailure() {
        UserData userData = new UserData(null, "password", "email@test.com");
        assertThrows(BadRequestException.class, () -> userService.createUser(userData));
    }

    @Test
    void loginUsersuccess() throws UnauthorizedException, BadRequestException {
        UserData userData = new UserData("testUser", "password", "email@test.com");
        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            fail("Test setup failed");
        }
        AuthData result = userService.loginUser(userData);
        assertNotNull(result);
        assertEquals("testUser", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginUserfailure() {
        UserData userData = new UserData("nonexistentUser", "wrongPassword", "email@test.com");
        assertThrows(UnauthorizedException.class, () -> userService.loginUser(userData));
    }

    @Test
    void logoutUsersuccess() throws DataAccessException, BadRequestException {
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("testUser", authToken));
        assertDoesNotThrow(() -> userService.logoutUser(authToken));
        assertNull(authDAO.getAuth(authToken));
    }

    @Test
    void logoutUserfailure() {
        String invalidAuthToken = "invalidToken";
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser(invalidAuthToken));
    }

    @Test
    void clearsuccess() {
        try {
            userDAO.createUser(new UserData("testUser", "password", "email@test.com"));
            authDAO.addAuth(new AuthData("testUser", "token"));
        } catch (DataAccessException | BadRequestException e) {
            fail("Test setup failed");
        }
        userService.clear();
        assertTrue(true);
    }
}

class GameServiceTest {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private GameService gameService;

    @BeforeEach
    void setUp() {
        try {
            gameDAO = new GameSQLDAO();
            authDAO = new AuthSQLDAO();
        }
        catch (DataAccessException e){
            System.out.println("failed to load databases");
        }
            gameService = new GameService(gameDAO, authDAO);
    }

    @AfterEach
    void tearDown() {
        gameDAO.clear();
        authDAO.clear();
    }

    @Test
    void listGamessuccess() throws UnauthorizedException, BadRequestException, DataAccessException {
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        GameData game1 = new GameData(1, null, null, "Game1", null, null);
        GameData game2 = new GameData(2, null, null, "Game2", null, null);
        gameDAO.createGame(game1);
        gameDAO.createGame(game2);
        HashSet<GameData> actualGames = gameService.listGames(authToken);
        assertEquals(2, actualGames.size());
    }

    @Test
    void listGamesfailure() {
        String invalidAuthToken = "invalidToken";
        assertThrows(UnauthorizedException.class, () -> gameService.listGames(invalidAuthToken));
    }

    @Test
    void createGamesuccess() throws UnauthorizedException, BadRequestException, DataAccessException {
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        String gameName = "TestGame";
        int gameId = gameService.createGame(authToken, gameName);
        assertTrue(gameId > 0);
        GameData game = gameDAO.getGame(gameId);
        assertNotNull(game);
        assertEquals(gameName, game.gameName());
    }

    @Test
    void createGamefailure() {
        String invalidAuthToken = "invalidToken";
        String gameName = "TestGame";
        assertThrows(UnauthorizedException.class, () -> gameService.createGame(invalidAuthToken, gameName));
    }

    @Test
    void joinGamesuccess() throws UnauthorizedException, BadRequestException, DataAccessException {
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        int gameId = gameService.createGame(authToken, "TestGame");
        boolean result = gameService.joinGame(authToken, gameId, "WHITE");
        assertTrue(result);
        GameData game = gameDAO.getGame(gameId);
        assertEquals("user1", game.whiteUsername());
    }

    @Test
    void joinGamefailure() throws UnauthorizedException, BadRequestException {
        String authToken = "validToken";
        authDAO.addAuth(new AuthData("user1", authToken));
        int gameId = gameService.createGame(authToken, "TestGame");
        assertThrows(BadRequestException.class, () -> gameService.joinGame(authToken, gameId, "INVALID_COLOR"));
    }

    @Test
    void clearsuccess() throws BadRequestException, DataAccessException {
        GameData game = new GameData(1, null, null, "TestGame", null, null);
        gameDAO.createGame(game);
        authDAO.addAuth(new AuthData("user1", "token"));
        gameService.clear();
        assertTrue(gameDAO.listGames().isEmpty());
    }
}