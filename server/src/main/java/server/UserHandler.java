package server;

import com.google.gson.Gson;
import dataaccess.*;
import service.UserService;
import model.*;
import spark.*;

public class UserHandler {
    UserService userService;
    public UserHandler(UserService userService) {
        this.userService = userService;
    }
    //register
    public Object register(Request request, Response response) throws BadRequestException {
        UserData user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null) {
            throw new BadRequestException("No username or password");
        }
        try {
            AuthData authData = userService.createUser(user);
            response.status(200);
            return new Gson().toJson(authData);
        } catch (BadRequestException s) {
            response.status(403);
            return "{\"message\": \"Error: already taken\"}";
        }
    }
    //login
    public Object login(Request request, Response response) throws UnauthorizedException {
        UserData user = new Gson().fromJson(request.body(), UserData.class);
        AuthData auth = userService.loginUser(user);
        response.status(200);
        return new Gson().toJson(auth);
    }
    //logout
    public Object logout(Request request, Response responce) throws UnauthorizedException, DataAccessException {
        String auth = request.headers("authorization");
        userService.logoutUser(auth);
        responce.status(200);
        return "{}";
    }
}

