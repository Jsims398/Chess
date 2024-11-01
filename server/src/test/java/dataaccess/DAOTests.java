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
//
//    @Nested
//    class UserDAOTests {
//        private UserDAO userDAO;
//        private final UserData testUser = new UserData("testUser", "password123", "test@example.com");
//        private final UserData invalidUser = new UserData(null, null, "test@example.com");
//
//        @BeforeEach
//        void setUp() {
//            userDAO = new UserSQLDAO();
//            userDAO.clear();
//        }
//
//        @Test
//        void testAddUser_Positive() throws DataAccessException {
//            userDAO.addUser(testUser);
//            assertTrue(userDAO.userExists(testUser.getUsername()));
//        }
//
//        @Test
//        void testAddUser_Negative() {
//            assertThrows(DataAccessException.class, () -> userDAO.addUser(invalidUser));
//        }
//
//        @Test
//        void testGetUser_Positive() throws DataAccessException {
//            userDAO.addUser(testUser);
//            UserData retrieved = userDAO.getUser(testUser.getUsername());
//            assertEquals(testUser.getEmail(), retrieved.getEmail());
//        }
//
//        @Test
//        void testGetUser_Negative() {
//            assertNull(userDAO.getUser("nonexistentUser"));
//        }
//    }
//
//    @Nested
//    class DatabaseSetupTests {
//
//        @Test
//        void testDatabaseCreation() {
//            assertDoesNotThrow(() -> {
//                AuthDAO authDAO = new AuthSQLDAO();
//                UserDAO userDAO = new UserSQLDAO();
//                GameDAO gameDAO = new GameSQLDAO();
//            });
//        }
//
//        @Test
//        void testTablePersistence() throws DataAccessException {
//            AuthDAO authDAO = new AuthSQLDAO();
//            authDAO.addAuthData(new AuthData("persist-token", "persistUser"));
//            AuthData retrieved = authDAO.getAuthData("persist-token");
//            assertNotNull(retrieved);
//            assertEquals("persistUser", retrieved.getUsername());
//        }
//    }
}
