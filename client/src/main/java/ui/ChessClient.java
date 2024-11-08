package ui;

import java.util.Arrays;
import com.google.gson.Gson;
import facade.ResponseException;
import facade.ServerFacade;
import model.*;
import static ui.State.*;

public class ChessClient {
    UserData user;
    private final ServerFacade server;
    private State state = State.LOGGEDOUT;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var commnads = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (state) {
                case LOGGEDOUT -> handleLoggedOutCommands(commnads, params);
                case LOGGEDIN -> handleLoggedInCommands(commnads, params);
                case GAMEPLAY -> handleGameplayCommands(commnads, params);
            };
        }
        catch (ResponseException exception) {
            return exception.getMessage();
        }
    }

    private String handleGameplayCommands(String command, String[] params) {
        return "NA";
    }

    private String handleLoggedInCommands(String command, String[] params) throws ResponseException {
        return switch (command) {
            case "help" -> help();
            case "logout" -> logout();
//            case "creategame" -> createGame(params);
//            case "listgames" -> listGames();
//            case "playgame" -> playGame(params);
//            case "observegame" -> observeGame(params);
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    private String handleLoggedOutCommands(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "help" -> help();
            case "quit" -> "quit";
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    String help() {
        return switch (state) {
            case LOGGEDOUT -> """
                Available commands in Prelogin:
                - help: Show this help message.
                - login <username> <password>: Log in to your account.
                - register <username> <password> <email>: Register a new account.
                - quit: Exit the application.
                """;
            case LOGGEDIN -> """
                Available commands in Postlogin:
                - help: Show this help message.
                - logout: Log out and return to Prelogin UI.
                - creategame <gameName>: Create a new game with the specified name.
                - listgames: List all available games.
                - playgame <number> <color>: Join a game by its number and specify a color (e.g., white or black).
                - observegame <number>: Observe a game by its number.
                """;
            case GAMEPLAY -> """
                Available commands in Gameplay:
                - help: Show this help message.
                - move <from> <to>: Make a move (e.g., move e2 e4).
                - resign: Resign from the current game.
                - offerdraw: Offer a draw to your opponent.
                """;
        };
    }

    private String register (String[]params) throws ResponseException {
        if (params.length == 3) {
            server.register(new UserData(params[0], params[1], params[2]));
            return "Registration successful. You are now logged in as " + params[0];
        }
        throw new ResponseException("Usage: register <username> <password> <email>");
    }

//    private String login (String[]params) throws ResponseException {
//        if (params.length == 2) {
//            UserData loginUser = new UserData(params[0], params[1], null);
//            UserData loggedInUser = server.login(loginUser);
//
//            if (loggedInUser != null) {
//                this.user = loggedInUser;
//                state = State.LOGGEDIN;
//                return "Successfully logged in as " + loggedInUser.username();
//            }
//            throw new ResponseException("Login failed.");
//        }
//        throw new ResponseException("Usage: login <username> <password>");
//    }

    private String login(String[] params) throws ResponseException {
        if (params.length == 2) {
            // Create a UserData object with the login details
            UserData loginUser = new UserData(params[0], params[1], null);

            // Call server.login to get an AuthData object, which contains the auth token
            AuthData authData = server.login(loginUser); // Modify server.login to return AuthData

            if (authData != null && authData.getToken() != null) {
                this.authToken = authData.getToken(); // Store the auth token in a class variable
                this.user = loginUser; // Optionally store the user information
                state = State.LOGGEDIN;
                return "Successfully logged in as " + loginUser.username();
            }
            throw new ResponseException("Login failed: Invalid credentials.");
        }
        throw new ResponseException("Usage: login <username> <password>");
    }

    private String logout() throws ResponseException {
        assertSignedIn();
        server.logout(user);
        state = State.LOGGEDOUT;
        user = null;
        return "Logged out successfully.";
    }

//    private String createGame(String[] params) throws ResponseException {
//        if (params.length == 1) {
//            String gameName = params[0];
//            server.createGame(gameName);
//            return "Game '" + gameName + "' created successfully.";
//        }
//        throw new ResponseException("Usage: creategame <gameName>");
//    }
//
//    private String listGames() throws ResponseException {
//        var games = server.listGames(); // Assuming list API returns a list of games with details
//        if (games.isEmpty()) {
//            return "No games available.";
//        }
//        gameListMap.clear();
//        StringBuilder sb = new StringBuilder("Available games:\n");
//        int count = 1;
//        for (var game : games) {
//            sb.append(count)
//                    .append(". ")
//                    .append(game.getName())
//                    .append(" - Players: ")
//                    .append(game.getPlayers())
//                    .append("\n");
//            gameListMap.put(count, game.getId()); // Map number to game ID for easy access
//            count++;
//        }
//        return sb.toString();
//    }
//
//    private String playGame(String[] params) throws ResponseException {
//        if (params.length == 2) {
//            try {
//                int gameNumber = Integer.parseInt(params[0]);
//                String color = params[1].toLowerCase();
//
//                if (!gameListMap.containsKey(gameNumber)) {
//                    throw new ResponseException("Invalid game number. Please list games first.");
//                }
//
//                String gameId = gameListMap.get(gameNumber);
//                server.joinGame(gameId, color); // Server API to join game with specified color
//                state = GAMEPLAY; // Transition to gameplay state if applicable
//                return "Joined game " + gameId + " as " + color + ".";
//            } catch (NumberFormatException e) {
//                throw new ResponseException("Invalid game number format.");
//            }
//        }
//        throw new ResponseException("Usage: playgame <number> <color>");
//    }
//
//    private String observeGame(String[] params) throws ResponseException {
//        if (params.length == 1) {
//            try {
//                int gameNumber = Integer.parseInt(params[0]);
//                if (!gameListMap.containsKey(gameNumber)) {
//                    throw new ResponseException("Invalid game number. Please list games first.");
//                }
//                String gameId = gameListMap.get(gameNumber);
//                server.observeGame(gameId); // Server API to observe game
//                return "Observing game " + gameId + ".";
//            } catch (NumberFormatException e) {
//                throw new ResponseException("Invalid game number format.");
//            }
//        }
//        throw new ResponseException("Usage: observegame <number>");
//    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
