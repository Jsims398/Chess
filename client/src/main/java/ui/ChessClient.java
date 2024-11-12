package ui;

import java.util.*;

import facade.ResponseException;
import facade.ServerFacade;
import model.*;

public class ChessClient {
    UserData user;
    AuthData auth;
    private final Map<Integer, GameData > gameListMap = new HashMap<>();

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
            case "creategame" -> createGame(params);
            case "listgames" -> listGames();
            case "playgame" -> playGame(params);
            case "observegame" -> observeGame(params);
            default -> "Unknown command. Type 'help' for options.";
        };
    }

    private String handleLoggedOutCommands(String cmd, String[] params) throws ResponseException {
        return switch (cmd) {
            case "login" -> login(params);
            case "register" -> register(params);
            case "help" -> help();
            case "quit" -> "\n";
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
                - playgame <number> <WHITE|BLACK>: Join a game by its number and specify a color (e.g., white or black).
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
            return "Registration successful.";
        }
        throw new ResponseException("Usage: register <username> <password> <email>");
    }

    private String login(String[] params) throws ResponseException{
        if (params.length != 2) {
            throw new ResponseException(400, "No username or pass was given");
        }
        try {
            auth = server.login(new UserData(params[0], params[1], null));
            if (auth.authToken() != null){
                state =State.LOGGEDIN;
                return String.format("%s%s %s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Logged in as", auth.username());
            }
            else {
                throw new ResponseException("Login failed: Missing auth token.");
            }
        }
        catch (ResponseException e) {
            throw new ResponseException(Integer.parseInt(e.getMessage().substring(23)), "");
        }
    }


    private String logout() throws ResponseException {
        assertSignedIn();
        server.logout(auth);
        state = State.LOGGEDOUT;
        user = null;
        return "Logged out successfully.";
    }

    private String createGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            String gameName = params[0];
            server.createGame(gameName,auth);
            return "Game '" + gameName + "' created successfully.";
        }
        throw new ResponseException("Usage: creategame <gameName>");
    }

    private String listGames() throws ResponseException {
        var string = new StringBuilder();
        try {
            var result = server.listGames(auth);
            if (result.length == 0) {
                return String.format("%s%s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "No games to list");
            } else {
                int count = 1;
                for (var game : result) {
                    string.append(EscapeSequences.SET_TEXT_COLOR_BLUE)
                            .append(count)
                            .append(". ")
                            .append(EscapeSequences.SET_TEXT_BOLD)
                            .append(game.gameName())
                            .append(EscapeSequences.RESET_TEXT_ITALIC)
                            .append("\n");

                    String whitePlayer = game.whiteUsername() != null ? game.whiteUsername() : "None";
                    String blackPlayer = game.blackUsername() != null ? game.blackUsername() : "None";
                    string.append("  White: ").append(whitePlayer)
                            .append("\n")
                            .append("  Black: ").append(blackPlayer)
                            .append("\n")
                            .append("--------------------------------\n");
                    gameListMap.put(count, game);
                    count++;
                }
            }
            return string.toString();
        }
        catch (ResponseException e) {
            throw new ResponseException(Integer.parseInt(e.getMessage().substring(23)), "Error processing games list");
        }
    }

    private String playGame(String[] params) throws ResponseException {
        if (params.length == 2) {
            try {
                GameData gamedata = gameListMap.get(Integer.parseInt(params[0]));
                int gameId = gamedata.gameID();
                String color = params[1].toUpperCase();

                if (!color.equals("BLACK") && !color.equals("WHITE")){
                    throw new ResponseException("Invalid color. Please choose 'BLACK' or 'WHITE'.");
                }

                server.joinGame(gameId, color, auth);
//                state = State.GAMEPLAY; //add later
                new PrintBoard(gamedata.game()).printBoard();
                return "Joined game " + params[0] + " as " + color + ".";

            } catch (NumberFormatException e) {
                throw new ResponseException("Invalid game number format.");
            }
        }
        throw new ResponseException("Usage: playgame <number> <color>");
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            try {
                int gameNumber = Integer.parseInt(params[0]);
                if (!gameListMap.containsKey(gameNumber)) {
                    throw new ResponseException("Invalid game number. Please list games first.");
                }
                GameData gamedata = gameListMap.get(Integer.parseInt(params[0]));
                new PrintBoard(gamedata.game()).printBoard();
                return "Observing game " + params[0] + ".";
            } catch (NumberFormatException e) {
                throw new ResponseException("Invalid game number format.");
            }
        }
        throw new ResponseException("Usage: observegame <number>");
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
