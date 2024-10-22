package passoff;

import com.google.gson.Gson;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import server.GameHandler;
import server.UserHandler;
import service.*;
import spark.*;
import java.util.HashSet;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

// Test implementation of Request class
class TestRequest extends Request {
    private final HashMap<String, String> headers = new HashMap<>();
    private String body;

    public void setHeader(String header, String value) {
        headers.put(header, value);
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String headers(String header) {
        return headers.get(header);
    }

    @Override
    public String body() {
        return body;
    }
}

// Test implementation of Response class
class TestResponse extends Response {
    private int statusCode;

    @Override
    public void status(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatus() {
        return statusCode;
    }
}

// Test implementation of UserService
class TestUserService extends UserService {
    private boolean throwAuthError = false;

    public TestUserService() {
        super(null, null);
    }

    public void setThrowAuthError(boolean throwError) {
        this.throwAuthError = throwError;
    }

    @Override
    public AuthData createUser(UserData user) {
        return new AuthData(user.username(), "testToken");
    }

    @Override
    public AuthData loginUser(UserData user) throws UnauthorizedException {
        if (throwAuthError) {
            throw new UnauthorizedException();
        }
        return new AuthData(user.username(), "testToken");
    }

    @Override
    public void logoutUser(String authentication) throws UnauthorizedException {
        if (throwAuthError) {
            throw new UnauthorizedException();
        }
    }
}

// Test implementation of GameService
class TestGameService extends GameService {
    private boolean throwAuthError = false;
    private final boolean throwBadRequestError = false;
    private boolean joinGameSuccess = true;

    public TestGameService() {
        super(null, null);
    }

    public void setThrowAuthError(boolean throwError) {
        this.throwAuthError = throwError;
    }

    public void setJoinGameSuccess(boolean success) {
        this.joinGameSuccess = success;
    }

    @Override
    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException {
        if (throwAuthError) {
            throw new UnauthorizedException();
        }
        HashSet<GameData> games = new HashSet<>();
        games.add(new GameData(1, null, null, "TestGame", null));
        return games;
    }

    @Override
    public int createGame(String authentication, String name) throws UnauthorizedException, BadRequestException {
        if (throwAuthError) {
            throw new UnauthorizedException();
        }
        if (throwBadRequestError) {
            throw new BadRequestException("Invalid game name");
        }
        return 1;
    }

    @Override
    public boolean joinGame(String authentication, int gameID, String color) throws UnauthorizedException, BadRequestException {
        if (throwAuthError) {
            throw new UnauthorizedException();
        }
        if (throwBadRequestError) {
            throw new BadRequestException("Invalid color");
        }
        return joinGameSuccess;
    }
}

class UserHandlerTest {
    private TestUserService userService;
    private UserHandler userHandler;
    private TestRequest request;
    private TestResponse response;

    @BeforeEach
    void setUp() {
        userService = new TestUserService();
        userHandler = new UserHandler(userService);
        request = new TestRequest();
        response = new TestResponse();
    }

    @Test
    void register_success() throws BadRequestException {
        // Arrange
        UserData userData = new UserData("testUser", "password", "email@test.com");
        request.setBody(new Gson().toJson(userData));

        // Act
        Object result = userHandler.register(request, response);

        // Assert
        assertEquals(200, response.getStatus());
        assertTrue(result.toString().contains("testToken"));
    }

    @Test
    void register_failure_missingFields() {
        // Arrange
        UserData userData = new UserData(null, null, "email@test.com");
        request.setBody(new Gson().toJson(userData));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> userHandler.register(request, response));
    }

    @Test
    void login_success() throws UnauthorizedException, BadRequestException {
        UserData userData = new UserData("testUser", "password", "email@test.com");
        request.setBody(new Gson().toJson(userData));
        Object result = userHandler.login(request, response);
        assertEquals(200, response.getStatus());
        assertTrue(result.toString().contains("testToken"));
    }

    @Test
    void login_failure() {
        UserData userData = new UserData("testUser", "wrongPassword", "email@test.com");
        request.setBody(new Gson().toJson(userData));
        userService.setThrowAuthError(true);
        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userHandler.login(request, response));
    }

    @Test
    void logout_success() throws UnauthorizedException, DataAccessException {
        // Arrange
        request.setHeader("authorization", "validToken");

        // Act
        Object result = userHandler.logout(request, response);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("{}", result);
    }

    @Test
    void logout_failure() {
        // Arrange
        request.setHeader("authorization", "invalidToken");
        userService.setThrowAuthError(true);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> userHandler.logout(request, response));
    }
}

class GameHandlerTest {
    private TestGameService gameService;
    private GameHandler gameHandler;
    private TestRequest request;
    private TestResponse response;

    @BeforeEach
    void setUp() {
        gameService = new TestGameService();
        gameHandler = new GameHandler(gameService);
        request = new TestRequest();
        response = new TestResponse();
    }

    @Test
    void listGames_success() throws UnauthorizedException {
        // Arrange
        request.setHeader("authorization", "validToken");

        // Act
        Object result = gameHandler.listGames(request, response);

        // Assert
        assertEquals(200, response.getStatus());
        assertTrue(result.toString().contains("TestGame"));
    }

    @Test
    void listGames_failure() {
        // Arrange
        request.setHeader("authorization", "invalidToken");
        gameService.setThrowAuthError(true);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> gameHandler.listGames(request, response));
    }

    @Test
    void createGame_success() throws UnauthorizedException, BadRequestException {
        // Arrange
        request.setHeader("authorization", "validToken");
        request.setBody("{\"gameName\":\"TestGame\"}");

        // Act
        Object result = gameHandler.createGame(request, response);

        // Assert
        assertEquals(200, response.getStatus());
        assertTrue(result.toString().contains("\"gameID\": 1"));
    }

    @Test
    void createGame_failure_unauthorized() {
        // Arrange
        request.setHeader("authorization", null);

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> gameHandler.createGame(request, response));
        assertEquals(401, response.getStatus());
    }

    @Test
    void joinGame_success() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        request.setHeader("authorization", "validToken");
        request.setBody("{\"playerColor\":\"WHITE\",\"gameID\":1}");

        // Act
        Object result = gameHandler.joinGame(request, response);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("{}", result);
    }

    @Test
    void joinGame_failure_colorTaken() throws UnauthorizedException, BadRequestException, DataAccessException {
        // Arrange
        request.setHeader("authorization", "validToken");
        request.setBody("{\"playerColor\":\"WHITE\",\"gameID\":1}");
        gameService.setJoinGameSuccess(false);

        // Act
        Object result = gameHandler.joinGame(request, response);

        // Assert
        assertEquals(403, response.getStatus());
        assertTrue(result.toString().contains("already taken"));
    }
}