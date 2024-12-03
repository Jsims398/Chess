package ui;

import model.GameData;
import org.glassfish.grizzly.utils.EchoFilter;
import websocket.NotificationHandler;
import websocket.messages.*;
import websocket.messages.ErrorMessage;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;
    public GameData game;

    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl, this);
    }

    public void run() {
        System.out.println("\uD83D\uDC36 Welcome to Chess. Sign in to start.");
        System.out.print(client.help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        var input = "";

        while (!result.equals("quit")) {
            printPrompt();
            input = scanner.nextLine();

            try {
                result = client.eval(input);
                System.out.print(result);
            }
            catch (Exception exception) {
                var message = exception.toString();
                System.out.print(message);
            }

            // Check for quit command
            if (input.equalsIgnoreCase("quit")) {
                quit();
                return;
            }
        }
    }

    private void printPrompt() {
        System.out.printf("\n%s%s >>> %s", EscapeSequences.SET_TEXT_BOLD,
                EscapeSequences.SET_TEXT_COLOR_WHITE, EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    public void quit() {
        System.out.println(String.format("%s%s%s%n", EscapeSequences.SET_TEXT_COLOR_BLUE,
                "Quitting application...", "\u001B[0m"));
        System.exit(0);
    }

    @Override
    public void notify(ServerMessage serverMessage) {
        System.out.println(serverMessage.getMessage());

    }

    @Override
    public void updateGame(GameData game) {
        this.game = game;
    }

    public GameData getGame(){
        return game;
    }

    @Override
    public void notifyError(ErrorMessage errorMessage) {
        System.out.println(errorMessage.getErrorMessage());

    }
}