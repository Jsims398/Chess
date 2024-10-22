package passoff.dataaccess;

import model.UserData;
import dataaccess.DataAccessException;
import dataaccess.UserDAOMemory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAOMemory userDAO;
    private UserData testUser;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAOMemory();
        userDAO.clear();
        testUser = new UserData("testUser", "password123", "test@example.com");
    }

    @AfterEach
    void tearDown() {
        userDAO.clear();
    }

    @Test
    void getUser_success() throws DataAccessException {
        userDAO.createUser(testUser);
        UserData retrieved = userDAO.getUser(testUser.username());
        assertEquals(testUser, retrieved);
    }

    @Test
    void getUser_nonexistent() {
        assertThrows(DataAccessException.class, () ->
                userDAO.getUser("nonexistentUser"));
    }

    @Test
    void createUser_success() throws DataAccessException {
        assertDoesNotThrow(() -> userDAO.createUser(testUser));
        UserData retrieved = userDAO.getUser(testUser.username());
        assertEquals(testUser, retrieved);
    }

    @Test
    void createUser_duplicate() {
        assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(testUser);
            userDAO.createUser(testUser);
        });
    }

    @Test
    void authenticateUser_success() throws DataAccessException {
        userDAO.createUser(testUser);
        assertTrue(userDAO.authenticateUser(testUser.username(), testUser.password()));
    }

    @Test
    void authenticateUser_wrongPassword() throws DataAccessException {
        userDAO.createUser(testUser);
        assertFalse(userDAO.authenticateUser(testUser.username(), "wrongPassword"));
    }

    @Test
    void authenticateUser_nonexistentUser() {
        assertThrows(DataAccessException.class, () ->
                userDAO.authenticateUser("nonexistentUser", "anyPassword"));
    }

    @Test
    void clear_success() throws DataAccessException {
        userDAO.createUser(testUser);
        userDAO.clear();
        assertThrows(DataAccessException.class, () ->
                userDAO.getUser(testUser.username()));
    }
}