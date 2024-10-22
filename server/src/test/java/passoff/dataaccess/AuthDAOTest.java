package passoff.dataaccess;

import model.AuthData;
import dataaccess.DataAccessException;
import dataaccess.AuthDAOMemory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private AuthDAOMemory authDAO;
    private AuthData testAuth;

    @BeforeEach
    void setUp() {
        authDAO = new AuthDAOMemory();
        authDAO.clear();
        testAuth = new AuthData("testAuthToken", "testUsername");
    }

    @AfterEach
    void tearDown() {
        authDAO.clear();
    }

    @Test
    void getAuth_success() throws DataAccessException {
        authDAO.addAuth(testAuth);
        AuthData retrieved = authDAO.getAuth(testAuth.authToken());
        assertNotNull(retrieved);
        assertEquals(testAuth.authToken(), retrieved.authToken());
        assertEquals(testAuth.username(), retrieved.username());
    }

    @Test
    void getAuth_nonexistent() {
        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth("nonexistentToken"));
    }

    @Test
    void addAuth_success() {
        assertDoesNotThrow(() -> {
            authDAO.addAuth(testAuth);
            AuthData retrieved = authDAO.getAuth(testAuth.authToken());
            assertNotNull(retrieved);
        });
    }

    @Test
    void deleteAuth_success() {
        authDAO.addAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());
        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void deleteAuth_nonexistent() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistentToken"));
    }

    @Test
    void clear_success() {
        authDAO.addAuth(testAuth);
        authDAO.addAuth(new AuthData("anotherToken", "anotherUser"));

        authDAO.clear();
        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth(testAuth.authToken()));
    }
}