package client;

import org.junit.jupiter.api.*;
import facade.ServerFacade;
import model.*;
import facade.ResponseException;
import server.Server;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on port " + port);
        facade = new ServerFacade(String.format("http://localhost:%d", port));
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() {
        try{facade.clear();}
        catch (ResponseException e){
            System.out.println("failed to clear DB");
        }
    }

    @Test
    void registerSuccess() throws Exception {
        facade.register(new UserData("user", "user", "user@email.com"));
        var auth = facade.login(new UserData("user", "user", null));
        assertNotNull(auth);
        assertNotNull(auth.authToken());
    }

    @Test
    void loginSuccess() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        var authData = facade.login(new UserData("test", "test", null));
        assertNotNull(authData);
        assertNotNull(authData.authToken());
    }

    @Test
    void loginFailureWrongPassword() throws ResponseException {
        facade.login(new UserData("test", "wrongpassword", null));
        assertThrows(ResponseException.class, () -> facade.listGames(new AuthData("this", "BadAuth")));
    }

    @Test
    void createGameSuccess() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        var authData = facade.login(new UserData("test", "test", null));
        facade.createGame("TestGame", authData);
        var games = facade.listGames(authData);
        boolean gameCreated = false;
        for (GameData game : games) {
            if ("TestGame".equals(game.gameName())) {
                gameCreated = true;
                break;
            }
        }
        assertTrue(gameCreated, "The game 'TestGame' should be created and listed.");
    }
    @Test
    void createGameFailureNoAuth() {
        assertThrows(ResponseException.class, () -> {
            facade.createGame("TestGame", null);
        });
    }
    @Test
    void listGamesSuccess() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        AuthData authData = facade.login(new UserData("test", "test", null));
        facade.createGame("TestGame", authData);
        var games = facade.listGames(authData);
        boolean gameCreated = false;
        for (GameData game : games) {
            if ("TestGame".equals(game.gameName())) {
                gameCreated = true;
                break;
            }
        }
        assertTrue(gameCreated, "The game 'TestGame' should be created and listed.");
    }

    @Test
    void listGamesFailureUnauthorized() {
        assertThrows(ResponseException.class, () -> {
            facade.listGames(null);
        });
    }

    @Test
    void logoutSuccess() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        var authData = facade.login(new UserData("test", "test", null));
        assertNotNull(authData);
        assertNotNull(authData.authToken());
        facade.logout(authData);
        assertThrows(ResponseException.class, () -> facade.listGames(authData));
    }

    @Test
    void logoutFail() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        var authData = facade.login(new UserData("test", "test", null));
        assertNotNull(authData);
        assertNotNull(authData.authToken());
        assertThrows(ResponseException.class, () -> facade.logout(new AuthData("test", "badAuth")));
    }
    @Test
    void joinGameSuccess() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        var authData = facade.login(new UserData("test", "test", null));

        facade.createGame("TestGame", authData);
        var games = facade.listGames(authData);
        GameData game = null;
        for (GameData g : games) {
            if ("TestGame".equals(g.gameName())) {
                game = g;
                break;
            }
        }
        assertNotNull(game, "Game should be created.");
        facade.joinGame(game.gameID(), "WHITE", authData);
        var updatedGames = facade.listGames(authData);
        boolean gameJoined = false;
        for (GameData g : updatedGames) {
            if (g.gameID() == game.gameID() && g.whiteUsername().equals("test")) {
                gameJoined = true;
                break;
            }
        }
        assertTrue(gameJoined, "The user should have joined the game as WHITE.");
    }

    @Test
    void joinGameFail() throws Exception {
        facade.register(new UserData("test", "test", "test@email.com"));
        var authData = facade.login(new UserData("test", "test", null));

        facade.createGame("TestGame", authData);
        var games = facade.listGames(authData);
        GameData game = Arrays.stream(games).filter(g -> "TestGame".equals(g.gameName())).findFirst().orElse(null);
        assertNotNull(game, "Game should be created.");
    }
}
