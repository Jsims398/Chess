package passoff;

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
            authDAO = new AuthDAOMemory();
            authDAO.clear();
        }

        @Nested
        class PositiveTests {
            @Test
            void addAuth_success() {
                assertDoesNotThrow(() -> authDAO.addAuth(testAuth));
                AuthData retrieved = assertDoesNotThrow(() -> authDAO.getAuth(testAuth.authToken()));
                assertEquals(testAuth.authToken(), retrieved.authToken());
                assertEquals(testAuth.username(), retrieved.username());
            }

            @Test
            void getAuth_success() {
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
            gameDAO = new GameDAOMemory();
            gameDAO.clear();
        }

        @Nested
        class PositiveTests {
            @Test
            void listGames_empty() {
                var games = gameDAO.listGames();
                assertNotNull(games);
                assertTrue(games.isEmpty());
            }

            @Test
            void listGames_multiple() {
                gameDAO.createGame(testGame);
                gameDAO.createGame(secondGame);
                var games = gameDAO.listGames();
                assertEquals(2, games.size());
                assertTrue(games.contains(testGame));
                assertTrue(games.contains(secondGame));
            }

            @Test
            void createGame_success() throws DataAccessException {
                assertDoesNotThrow(() -> gameDAO.createGame(testGame));
                GameData retrieved = gameDAO.getGame(testGame.gameID());
                assertEquals(testGame, retrieved);
            }

            @Test
            void updateGame_success() throws DataAccessException {
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
            void getGame_nonexistent() {
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
            userDAO = new UserDAOMemory();
            userDAO.clear();
        }

        @Nested
        class PositiveTests {
            @Test
            void createUser_success() throws DataAccessException {
                assertDoesNotThrow(() -> userDAO.createUser(testUser));
                UserData retrieved = userDAO.getUser(testUser.username());
                assertEquals(testUser, retrieved);
            }

            @Test
            void getUser_success() throws DataAccessException {
                userDAO.createUser(testUser);
                UserData retrieved = userDAO.getUser(testUser.username());
                assertEquals(testUser, retrieved);
            }

            @Test
            void authenticateUser_success() throws DataAccessException {
                userDAO.createUser(testUser);
                assertTrue(userDAO.authenticateUser(testUser.username(), testUser.password()));
            }

            @Test
            void clear_success() throws DataAccessException {
                userDAO.createUser(testUser);
                userDAO.clear();
                assertThrows(DataAccessException.class, () ->
                    userDAO.getUser(testUser.username()));
            }
        }

        @Nested
        class NegativeTests {
            @Test
            void createUser_duplicate() {
                assertDoesNotThrow(() -> userDAO.createUser(testUser));
                UserData duplicateUser = new UserData(testUser.username(), "differentPassword", "other@example.com");
                assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicateUser));
            }

            @Test
            void getUser_nonExistent() {
                assertThrows(DataAccessException.class, () ->
                    userDAO.getUser("nonexistentUser"));
            }

            @Test
            void authenticateUser_wrongPassword() throws DataAccessException {
                userDAO.createUser(testUser);
                assertFalse(userDAO.authenticateUser(testUser.username(), "wrongPassword"));
            }
        }

        @Nested
        class EdgeCases {
            @Test
            void authenticateUser_emptyPassword() throws DataAccessException {
                userDAO.createUser(testUser);
                assertFalse(userDAO.authenticateUser(testUser.username(), ""));
            }

            @Test
            void createUser_afterClear() {
                assertDoesNotThrow(() -> {
                    userDAO.createUser(testUser);
                    userDAO.clear();
                    userDAO.createUser(testUser);
                });
            }
        }
    }
}