package dao;

import chess.ChessGame;
import model.*;
import dataaccess.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DAOTests {

    @Nested
    class AuthDAOTests {
        private AuthDAO authDAO;
        private final AuthData testAuth = new AuthData("test-token-123", "testUser");
        @BeforeEach
        void setUp() {
            try {
                authDAO = new AuthSQLDAO();
            }
            catch (DataAccessException e){
                System.out.println("couldnt start SQL");
            }
            authDAO.clear();
        }

        @Nested
        class PositiveTests {
            @Test
            void addAuthsuccess() {
                assertDoesNotThrow(() -> authDAO.addAuth(testAuth));
                AuthData retrieved = assertDoesNotThrow(() -> authDAO.getAuth(testAuth.authToken()));
                assertEquals(testAuth.authToken(), retrieved.authToken());
                assertEquals(testAuth.username(), retrieved.username());
            }

            @Test
            void getAuthsuccess() {
                assertDoesNotThrow(() -> authDAO.addAuth(testAuth));
                AuthData retrieved = assertDoesNotThrow(() -> authDAO.getAuth(testAuth.authToken()));
                assertNotNull(retrieved);
                assertEquals(testAuth.authToken(), retrieved.authToken());
                assertEquals(testAuth.username(), retrieved.username());
            }
        }
    }

    @Nested
    class GameDAOTests {
        private GameDAO gameDAO;
        private final GameData testGame = new GameData(1, "whiteUser", "blackUser", "TestGame", new ChessGame());
        private final GameData secondGame = new GameData(2, null, null, "SecondGame", new ChessGame());

        @BeforeEach
        void setUp() {
            try {
                gameDAO = new GameSQLDAO();
            }
            catch (DataAccessException e){
                System.out.println("couldnt start SQL");
            }
            gameDAO.clear();
        }

        @Nested
        class PositiveTests {
            @Test
            void listGamesempty() {
                var games = gameDAO.listGames();
                assertNotNull(games);
                assertTrue(games.isEmpty());
            }

            @Test
            void listGamesmultiple() {
                gameDAO.createGame(testGame);
                gameDAO.createGame(secondGame);
                var games = gameDAO.listGames();
                assertEquals(2, games.size());
                assertTrue(games.contains(testGame));
                assertTrue(games.contains(secondGame));
            }

            @Test
            void createGamesuccess() throws DataAccessException {
                assertDoesNotThrow(() -> gameDAO.createGame(testGame));
                GameData retrieved = gameDAO.getGame(testGame.gameID());
                assertEquals(testGame, retrieved);
            }

            @Test
            void updateGamesuccess() throws DataAccessException {
                gameDAO.createGame(testGame);
                GameData updatedGame = new GameData(
                    testGame.gameID(),
                    "newWhiteUser",
                    testGame.blackUsername(),
                    testGame.gameName(),
                    testGame.game()
                );
                gameDAO.updateGame(updatedGame);
                GameData retrieved = gameDAO.getGame(testGame.gameID());
                assertEquals("newWhiteUser", retrieved.whiteUsername());
            }
        }

        @Nested
        class NegativeTests {
            @Test
            void getGamenonexistent() {
                assertThrows(DataAccessException.class, () -> gameDAO.getGame(999));
            }
        }
    }

    @Nested
    class UserDAOTests {
        private UserDAO userDAO;
        private final UserData testUser = new UserData("testUser", "password123", "test@example.com");
        @BeforeEach
        void setUp() {
            try{
                userDAO = new UserSQLDAO();
            }
            catch (DataAccessException e){
                System.out.println("could start SQL");
            }
            try {
                userDAO.clear();
            } catch (DataAccessException exception) {
                throw new RuntimeException(exception);
            }
        }

        @Nested
        class PositiveTests {
            @Test
            void createUsersuccess() throws DataAccessException {
                assertDoesNotThrow(() -> userDAO.createUser(testUser));
                UserData retrieved = userDAO.getUser(testUser.username());
                assertEquals(testUser, retrieved);
            }

            @Test
            void getUsersuccess() throws DataAccessException {
                userDAO.createUser(testUser);
                UserData retrieved = userDAO.getUser(testUser.username());
                assertEquals(testUser, retrieved);
            }

            @Test
            void authenticateUsersuccess() throws DataAccessException {
                userDAO.createUser(testUser);
                assertTrue(userDAO.authenticateUser(testUser.username(), testUser.password()));
            }

            @Test
            void clearsuccess() throws DataAccessException {
                userDAO.createUser(testUser);
                userDAO.clear();
                assertThrows(DataAccessException.class, () ->
                    userDAO.getUser(testUser.username()));
            }
        }

        @Nested
        class NegativeTests {
            @Test
            void createUserduplicate() {
                assertDoesNotThrow(() -> userDAO.createUser(testUser));
                UserData duplicateUser = new UserData(testUser.username(), "differentPassword", "other@example.com");
                assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser));
            }

            @Test
            void getUsernonExistent() {
                assertThrows(DataAccessException.class, () ->
                    userDAO.getUser("nonexistentUser"));
            }

            @Test
            void authenticateUserwrongPassword() throws DataAccessException {
                userDAO.createUser(testUser);
                assertFalse(userDAO.authenticateUser(testUser.username(), "wrongPassword"));
            }
        }

        @Nested
        class EdgeCases {
            @Test
            void authenticateUseremptyPassword() throws DataAccessException {
                userDAO.createUser(testUser);
                assertFalse(userDAO.authenticateUser(testUser.username(), ""));
            }

            @Test
            void createUserafterClear() {
                assertDoesNotThrow(() -> {
                    userDAO.createUser(testUser);
                    userDAO.clear();
                    userDAO.createUser(testUser);
                });
            }
        }
    }
}