package dataaccess;

import model.UserData;

import java.util.HashSet;

public class UserDAOMemory implements UserDAO {

    private HashSet<UserData> database;

    public UserDAOMemory() {
        database = HashSet.newHashSet(20);
    }
    //getUser
    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : database) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User not found: " + username);
    }
    //createUser
    @Override
    public void createUser(UserData user) throws DataAccessException {
        try {
            getUser(user.username());
        }
        catch (DataAccessException exception) {
            database.add(user);
            return;
        }

        throw new DataAccessException("User already exists: " + user.username());
    }
    //authUser
    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        boolean userExists = false;
        for (UserData user : database) {
            if (user.username().equals(username)) {
                userExists = true;
            }
            if (user.username().equals(username) &&
                    user.password().equals(password)) {
                return true;
            }
        }
        if (userExists) {
            return false;
        }
        else {
            throw new DataAccessException("User does not exist: " + username);
        }
    }
    //clear
    @Override
    public void clear() {
        database = HashSet.newHashSet(20);
    }
}
