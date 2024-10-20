package dataaccess;

import model.AuthData;

public interface AuthDAO {
//    The association of a username and an authorization token that represents
//    that the user has previously been authorized to use the application.

    AuthData getAuth(String authToken) throws DataAccessException;

    void addAuth(AuthData authData);
    void deleteAuth(String authToken);
    void clear();

}
