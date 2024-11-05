package ui;
import java.util.Scanner;

public class Repl {
    private static PreLoginUI preLoginUI;
    private static PostLoginUI postLoginUI;
//    private static PlayUI playUI;
//    private static ObservingUI observingUI;

    public static State state;

    public Repl(){
        preLoginUI = new PreLoginUI();
        postLoginUI = new PostLoginUI();
//        playUI = new PlayUI();
//        observingUI = new ObservingUI();

        state = State.LOGGED_OUT;
    }

    public void run() {
        System.out.printf("%s%s%s%n%s",
                EscapeSequences.ERASE_SCREEN,
                String.format("%s%s", EscapeSequences.SET_TEXT_BOLD, EscapeSequences.SET_TEXT_COLOR_MAGENTA),
                String.format("%s%s%s", EscapeSequences.WHITE_KING, "Welcome to the CS 240 Chess Client! Type <help> to get started.", EscapeSequences.WHITE_KING),
                EscapeSequences.RESET_TEXT_BOLD_FAINT);

        Scanner scanner = new Scanner(System.in);
        var input = "";
        var result = "";
        while (!input.equals("quit")) {
            printPrompt();
            input = scanner.nextLine();

            switch (state) {
                case LOGGED_OUT -> result = preLoginUI.eval(input);
                case LOGGED_IN -> result = postLoginUI.eval(input);
//                case IN_GAME -> {
//                    BoardRenderer.render(UI.getGame(UI.currentGameID));
//                    result = playUI.eval(input);
//                }
//                case OBSERVING_GAME -> {
//                    BoardRenderer.render(UI.getGame(UI.currentGameID));
//                    result = observingUI.eval(input);
//                }
            }
            System.out.printf("%s%n", result);
        }
    }

    private void printPrompt() {
        System.out.printf("%s%s[%s] >>> %s", EscapeSequences.SET_TEXT_BOLD, EscapeSequences.SET_TEXT_COLOR_WHITE, state,
                EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }
}