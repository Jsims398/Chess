package ui;

import websocket.NotificationHandler;
import websocket.messages.Notification;

import java.util.Scanner;

public class Repl implements NotificationHandler {
    private final ChessClient client;
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
            catch (Throwable exception) {
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
    public void notify(Notification notification) {
        System.out.println(notification.message());
        printPrompt();
    }
}