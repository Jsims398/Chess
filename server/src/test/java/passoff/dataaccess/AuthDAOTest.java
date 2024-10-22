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
    void getAuthsuccess() {
        authDAO.addAuth(testAuth);
        AuthData retrieved = authDAO.getAuth(testAuth.authToken());
        assertNotNull(retrieved);
        assertEquals(testAuth.authToken(), retrieved.authToken());
        assertEquals(testAuth.username(), retrieved.username());
    }

    @Test
    void getAuthnonexistent() {
        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth("nonexistentToken"));
    }

    @Test
    void addAuthsuccess() {
        assertDoesNotThrow(() -> {
            authDAO.addAuth(testAuth);
            AuthData retrieved = authDAO.getAuth(testAuth.authToken());
            assertNotNull(retrieved);
        });
    }

    @Test
    void deleteAuthsuccess() {
        authDAO.addAuth(testAuth);
        authDAO.deleteAuth(testAuth.authToken());
        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth(testAuth.authToken()));
    }

    @Test
    void deleteAuthnonexistent() {
        assertDoesNotThrow(() -> authDAO.deleteAuth("nonexistentToken"));
    }

    @Test
    void clearsuccess() {
        authDAO.addAuth(testAuth);
        authDAO.addAuth(new AuthData("anotherToken", "anotherUser"));

        authDAO.clear();
        assertThrows(DataAccessException.class, () ->
                authDAO.getAuth(testAuth.authToken()));
    }
}