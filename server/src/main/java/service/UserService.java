package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.UUID;

public class UserService {
    UserDAO userDAO;
    AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }
    //createUser
    public AuthData createUser(UserData user) throws BadRequestException {
        if(user.username() == null || user.password() == null || user.email() == null){
            throw new BadRequestException("User information incomplete");
        }
        try {
            userDAO.createUser(user);
        }
        catch (DataAccessException exception) {
            throw new BadRequestException(exception.getMessage());
        }

        String authentication = UUID.randomUUID().toString();
        AuthData authData = new AuthData(user.username(), authentication);
        authDAO.addAuth(authData);

        return authData;
    }
    //login
    public AuthData loginUser(UserData user) throws UnauthorizedException, BadRequestException {
        boolean authenticated;
        try {
            authenticated = userDAO.authenticateUser(user.username(), user.password());
        }
        catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }

        if (authenticated) {
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(user.username(), authToken);
            authDAO.addAuth(authData);
            return authData;
        }
        else {
            throw new UnauthorizedException();
        }
    }
    //logout
    public void logoutUser(String authentication) throws UnauthorizedException, DataAccessException {
        AuthData authData = authDAO.getAuth(authentication);
        if (authData == null) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authentication);
    }
    //clear
    public void clear() {
        try {
            userDAO.clear();
        } catch (DataAccessException exception) {
            throw new RuntimeException(exception);
        }
        authDAO.clear();
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        var auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new DataAccessException("Error: auth token not in database");
        }
        return auth;
    }
}
