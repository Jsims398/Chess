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
        catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(user.username(), authToken);
        authDAO.addAuth(authData);

        return authData;
    }
    //login
    //logout
    //clear


}
