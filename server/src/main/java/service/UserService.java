package service;

import dataaccess.*;
import model.*;

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
    public AuthData loginUser(UserData user) throws UnauthorizedException{
        boolean authentucated = false;
        try {
            authentucated = userDAO.authenticateUser(user.username(), user.password());
        }
        catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }

        if (authentucated) {
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
    public void logoutUser(String authentication) throws UnauthorizedException {
        try {
            authDAO.getAuth(authentication);
        }
        catch (DataAccessException exception) {
            throw new UnauthorizedException();
        }
        authDAO.deleteAuth(authentication);
    }
    //clear
    public void clear(){
        userDAO.clear();
        authDAO.clear();
    }


}
