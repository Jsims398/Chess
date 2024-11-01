package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DAOTests {

    @Nested
    class AuthDAOTests {
        private AuthDAO authDAO;
        private final AuthData testAuth = new AuthData("testtoken", "testUser");
        private final AuthData badAuth = new AuthData("badAuth", null);

        @BeforeEach
        void setUp() {
            try {
                authDAO = new AuthSQLDAO();
            }
            catch (DataAccessException exception){
                System.out.println("failed to setup");
            }
            authDAO.clear();
        }

        @Test
        void testAddAuthDataNegative(){
            assertThrows(BadRequestException.class, () -> authDAO.addAuth(badAuth));
        }

        @Test
        void testAddAuthDataPositive() throws DataAccessException{
            assertDoesNotThrow(() -> authDAO.addAuth(testAuth));
            assertNotNull(authDAO.getAuth(testAuth.authToken()));
        }

        @Test
        void testGetAuthDataPositive() throws DataAccessException {
            try {
                authDAO.addAuth(testAuth);
            }
            catch (BadRequestException e){
                System.out.println("couldnt add auth");
            }
            AuthData retrieved = authDAO.getAuth(testAuth.authToken());
            assertEquals(testAuth.username(), retrieved.username());
        }

        @Test
        void testGetAuthDataNegative() throws DataAccessException {
            assertNull(authDAO.getAuth(null), "should be null");
            }
        }


    @Nested
    class GameDAOTests {
        private GameDAO gameDAO;
        ChessGame game = new ChessGame();
        private final GameData testGame = new GameData(1, "whiteUser", "blackUser", "TestGame", game);
        private final GameData invalidGame = new GameData(-1, null, null, null, null);

        @BeforeEach
        void setUp() {
            try {
                gameDAO = new GameSQLDAO();
                gameDAO.clear();
            }
            catch (DataAccessException e){
                System.out.println("failed to start SQL Games");
            }
        }

        @Test
        void testAddGameDataPositive() throws DataAccessException {
            gameDAO.createGame(testGame);
            assertTrue(gameDAO.gameExists(testGame.gameID()));
        }

        @Test
        void testAddGameDataNegative() {
            assertThrows(DataAccessException.class, () -> gameDAO.createGame(invalidGame));
        }

        @Test
        void testGetGameDataPositive() throws DataAccessException {
            gameDAO.createGame(testGame);
            GameData retrieved = gameDAO.getGame(testGame.gameID());
            assertEquals(testGame.gameName(), retrieved.gameName());
        }

        @Test
        void testGetGameDataNegative(){
            assertThrows(DataAccessException.class, () -> gameDAO.getGame(0));        }
    }

    @Nested
    class UserDAOTests {
        private UserDAO userDAO;
        private final UserData testUser = new UserData("testUser", "password", "test@example.com");
        private final UserData invalidUser = new UserData(null, null, "test@example.com");

        @BeforeEach
        void setUp() {
            try {
                userDAO = new UserSQLDAO();
                userDAO.clear();
            }
            catch (DataAccessException exception){
                System.out.println("failed to start SQL for users");
            }
        }

        @Test
        void testAddUserPositive() throws DataAccessException {
            userDAO.createUser(testUser);
            assertNotNull(userDAO.getUser(testUser.username()));
        }

        @Test
        void testAddUserNegative() {
            assertThrows(DataAccessException.class, () -> userDAO.createUser(invalidUser));
        }

        @Test
        void testGetUserPositive() throws DataAccessException {
            userDAO.createUser(testUser);
            UserData retrieved = userDAO.getUser(testUser.username());
            assertEquals(testUser.email(), retrieved.email());
        }

        @Test
        void testGetUserNegative() throws DataAccessException {
            assertNull(userDAO.getUser("badUser"));
        }
    }
}
