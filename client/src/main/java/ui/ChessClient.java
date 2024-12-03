package ui;

import java.util.*;

import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import facade.ResponseException;
import facade.ServerFacade;
import model.*;
import websocket.*;

public class ChessClient {
    UserData user;
    AuthData auth;
    String color;
    String serverUrl;
    private final Map<Integer, GameData> gameListMap = new HashMap<>();
    private WebsocketFacade ws;
    private final NotificationHandler nh;
    private final ServerFacade server;
    private State state = State.LOGGEDOUT;

    public ChessClient(String serverUrl, NotificationHandler notificationHandler) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
        this. nh = notificationHandler;
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
        } catch (ResponseException exception) {
            return exception.getMessage();
        }
    }

    private String handleGameplayCommands(String command, String[] params) throws ResponseException {
        return switch(command) {
            case "help" -> help();
            case "printboard" -> printboard();
            case "leave" -> leave(auth);
            case "move" -> move(params);
            case "resign" -> resign();
//            case "showmoves" -> showmoves(params);
            default -> "Unknown command. Type 'help' for options.";
        };
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
                    - leave: Leave game.
                    - resign: Resign from the current game.
                    - printboard: Offer a draw to your opponent.
                    """;
        };
    }

    private String register(String[] params) throws ResponseException {
        if (params.length == 3) {
            boolean status = server.register(new UserData(params[0], params[1], params[2]));
            if (status) {
                auth = server.login(new UserData(params[0], params[1], null));
                if (auth.authToken() != null) {
                    state = State.LOGGEDIN;
                    return String.format("%s%s %s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Logged in as", auth.username());
                }
                return "Registration successful.";
            }
        }
        throw new ResponseException("Usage: register <username> <password> <email>");
    }

    private String login(String[] params) throws ResponseException {
        if (params.length != 2) {
            throw new ResponseException(400, "No username or pass was given");
        }
        try {
            auth = server.login(new UserData(params[0], params[1], null));
            if (auth != null) {
                if (auth.authToken() != null) {
                    state = State.LOGGEDIN;
                    return String.format("%s%s %s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Logged in as", auth.username());
                }
            }
            else {
                throw new ResponseException("Login failed: Missing auth token.");
            }
        }
        catch (ResponseException e) {
            throw new ResponseException("Login failed: User not found");
        }
        return "";
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

    private void assertSignedIn() throws ResponseException {
        if (state == State.LOGGEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }

    private String observeGame(String[] params) throws ResponseException {
        if (params.length == 1) {
            try {
                int gameNumber = Integer.parseInt(params[0]);
                if (!gameListMap.containsKey(gameNumber)) {
                    throw new ResponseException("Invalid game number. Please list games first.");
                }
                GameData gamedata = gameListMap.get(Integer.parseInt(params[0]));
                ws = new WebSocketFacade(serverUrl, repl, gameID, ChessGame.TeamColor.WHITE);
                ws.connect(authToken);
                gameName = game.gameName();
                state = State.OBSERVE;
                return String.format("Observing game %s", gameName) + "\n" + help();
                throw new ResponseException(400, "Expected: <game id>");
            }
        }
    }

    private String playGame(String[] params) throws ResponseException {
        if (params.length == 2) {

            try {
                int index = Integer.parseInt((params[0]));
                if(gameListMap.containsKey(index)) {
                    GameData gamedata = gameListMap.get(index);
                    int gameId = gamedata.gameID();
                    color = params[1].toUpperCase();

                    if (!color.equals("BLACK") && !color.equals("WHITE")) {
                        throw new ResponseException("Invalid color. Please choose 'BLACK' or 'WHITE'.");
                    }

                    boolean response = server.joinGame(gameId, color, auth);
                    if (response) {
                        ws = new WebsocketFacade(serverUrl, nh, gameId, color);
                        ws.connect(String.valueOf(auth.authToken()));
                        state = State.GAMEPLAY;

                        return "Joined game " + params[0] + " as " + color + ".";
                    }
                }
                else{
                    throw new ResponseException("Invalid game number.");
                }
            } catch (NumberFormatException e) {
                throw new ResponseException("Invalid game number format.");
            }
        }
        throw new ResponseException("Usage: playgame <number> <color>");
    }

     public String move(String...params) throws ResponseException {
         String piece = "EMPTY";
        if(params.length ==3){
            piece = params[2];
        }
        ChessMove move = makeMove(params[0], params[1], piece);
        ws.move(move, auth);
        printboard();
        return "";
    }

    public ChessMove makeMove(String from, String to, String promotion) {
        ChessPosition start = newPosition(from);
        ChessPosition end = newPosition(to);

        ChessPiece.PieceType type = null;

        if (!Objects.equals(promotion, "EMPTY")){
            if (Objects.equals(promotion, "queen")){ type = ChessPiece.PieceType.QUEEN;}
            if (Objects.equals(promotion, "rook")){ type = ChessPiece.PieceType.ROOK;}
            if (Objects.equals(promotion, "bishop")){ type = ChessPiece.PieceType.BISHOP;}
            if (Objects.equals(promotion, "knight")){ type = ChessPiece.PieceType.KNIGHT;}
        }

        return new ChessMove(start, end, type);
    }

    public ChessPosition newPosition(String position) {
        int column = position.charAt(0) - 'a' + 1;
        int row = Character.getNumericValue(position.charAt(1));
        return new ChessPosition(row, column);
    }

    public String resign() throws ResponseException {
        System.out.println("Are you sure you want to resign? yes or no");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        if (Objects.equals(line, "yes")){
            ws.resignGame(auth.authToken());
            return "Attempting to resign.";
        } else {
            return "Cancelled resign.";
        }


    }
    private String printboard() throws ResponseException {
        if (state == State.GAMEPLAY) {
            ws.printboard(auth, color);
            return "";
        }
        throw new ResponseException(400, "You must join a game");
    }

    private String leave(AuthData auth) throws ResponseException {
        ws.leave(auth);
        state = State.LOGGEDIN;
        color = null;
        return "Leaving game.";
    }
//    private String showmoves(String[] params){
//        return "COMPLETE";
//    }


}