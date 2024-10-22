package dataaccess;

import model.UserData;

public interface UserDAO {
//    A user is registered and authenticated as a player or observer in the application.
    UserData getUser(String username) throws DataAccessException;

    void createUser(UserData user) throws DataAccessException;

    boolean authenticateUser(String username, String password) throws DataAccessException;

    void clear();

}
