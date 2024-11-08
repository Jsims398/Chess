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
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.printf("\n%s%s >>> %s", EscapeSequences.SET_TEXT_BOLD, EscapeSequences.SET_TEXT_COLOR_WHITE, EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

}