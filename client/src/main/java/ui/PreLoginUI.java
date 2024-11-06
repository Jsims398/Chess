package ui;

import facade.*;
import requests.RequestResponseRecords.*;

import java.util.Arrays;

public class PreLoginUI extends UI{
    public PreLoginUI() {
        super();
    }

    @Override
    public String eval(String line) {
        var tokens = line.toLowerCase().split(" ");
        var cmd = tokens[0];
        var params = Arrays.copyOfRange(tokens, 1, tokens.length);
        try {
            return switch (cmd) {
                case "help" -> help();
                case "quit" -> quit();
                case "login" -> login(params);
                case "register" -> register(params);
                case "clear" -> clear();
                default -> help();
            };
        } catch (ResponseException e) {
            return handleError(e.errorCode);
        }
    }

    private String help() {
        return String.format("%s%s%n%s%n%s%n%s%n", EscapeSequences.SET_TEXT_COLOR_BLUE,
                "register <USERNAME> <PASSWORD> <EMAIL> - a user",
                "login <USERNAME> <PASSWORD> - to play chess",
                "quit - the application",
                "help - with available commands");
    }

    @Override
    public String quit() {
        return String.format("%s%s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Quitting application...");
    }

    private String clear() {
        try {
            facade.clear();
            return String.format("%s%s", EscapeSequences.SET_TEXT_COLOR_BLUE, "Cleared database");
        } catch (ResponseException e) {
            return String.format("%s%s", EscapeSequences.SET_TEXT_COLOR_BLUE, "Error: unable to clear database");
        }
    }

    private String login(String[] params) throws ResponseException{
        checkConnection();
        if (params.length != 2) {
            throw new ResponseException(400, "Bad request, needs 2 parameters");
        }
        try {
            LoginResult result = facade.login(new LoginRequest(params[0], params[1]));
            Repl.state = State.LOGGED_IN;
            authToken = result.authToken();
            return String.format("%s%s %s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Logged in", result.username());
        } catch (ResponseException e) {
            throw new ResponseException(Integer.parseInt(e.getMessage().substring(23)), "");
        }
    }

    private String register(String[] params) throws ResponseException{
        checkConnection();
        if (params.length != 3) {
            throw new ResponseException(400, "bad request");
        }
        try {
            RegisterResult result = facade.register(new RegisterRequest(params[0], params[1], params[2]));
            Repl.state = State.LOGGED_IN;
//            authToken = result.authToken();
            return String.format("%s%s %s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Registered", result.username());
        } catch (ResponseException e) {
            throw new ResponseException(Integer.parseInt(e.getMessage().substring(23)), "");
        }
    }
}