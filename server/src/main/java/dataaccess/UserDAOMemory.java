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
        UserData user = getUser(username);
        return user.password().equals(password);
    }
    //clear
    @Override
    public void clear() {
        database = HashSet.newHashSet(20);
    }
}
