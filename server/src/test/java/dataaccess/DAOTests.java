package dataaccess;

import chess.ChessGame;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DAOTests {

    @Nested
    class AuthDAOTests {
        private AuthDAO authDAO;
        private GameDAO gameDAO;
        private UserDAO userDAO;

        private final AuthData testAuth = new AuthData("test-token-123", "testUser");
        private final UserData testUser = new UserData("goodUser", "goodPass", "good@email.email");

        private final AuthData badTestAuth = new AuthData("test-token-123", "testUser");
        private final UserData badTestUser = new UserData("badUser", null, "email@email.email");
//create test people and auth
        @BeforeEach
        void setUp() {
            try {
                authDAO = new AuthSQLDAO();
                gameDAO = new GameSQLDAO();
                userDAO = new UserSQLDAO();
            }
            catch (DataAccessException exception) {
                System.out.println("couldnt start SQL");
            }

            try {
                authDAO.clear();
                gameDAO.clear();
                userDAO.clear();
            } catch (DataAccessException exception) {
                System.out.println("couldnt start SQL");
            }
        }//set up and clear memories
    }

    @Nested
    class GameDAOTests {
        private GameDAO gameDAO;
        private final GameData testGame = new GameData(1, "whiteUser", "blackUser", "TestGame", new ChessGame());
        private final GameData secondGame = new GameData(2, null, null, "SecondGame", new ChessGame());

        @BeforeEach
        void setUp() {
        }

        @Nested
        class PositiveTests {

        }

        @Nested
        class UserDAOTests {
            private UserDAO userDAO;
            private final UserData testUser = new UserData("testUser", "password123", "test@example.com");

            @BeforeEach
            void setUp() {
                try {
                    userDAO = new UserSQLDAO();
                } catch (DataAccessException e) {
                    System.out.println("could start SQL");
                }
                try {
                    userDAO.clear();
                } catch (DataAccessException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }
    }
}