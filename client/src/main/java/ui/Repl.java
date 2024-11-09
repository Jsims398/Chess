package ui;

import java.util.Scanner;

public class Repl{
    private final ChessClient client;
    public Repl(String serverUrl) {
        client = new ChessClient(serverUrl);
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
                return; // exit the loop and terminate the application
            }
        }
    }

    private void printPrompt() {
        System.out.printf("\n%s%s >>> %s", EscapeSequences.SET_TEXT_BOLD, EscapeSequences.SET_TEXT_COLOR_WHITE, EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    public void quit() {
        // Print the quitting message with blue text
        System.out.println(String.format("%s%s%s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, "Quitting application...", "\u001B[0m"));

        // Terminate the application
        System.exit(0);  // 0 indicates normal termination
    }
}