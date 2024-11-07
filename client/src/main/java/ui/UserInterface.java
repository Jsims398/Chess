package ui;
import facade.ServerFacade;

import java.util.Scanner;

public abstract class UserInterface {

    protected ServerFacade facade;  // The facade to interact with the server
    protected Scanner scanner;           // Scanner to read user input

    // Constructor to initialize the facade and scanner
    public UserInterface(ServerFacade facade) {
        this.facade = facade;
        this.scanner = new Scanner(System.in);
    }

    // Abstract method for showing available commands/options
    public abstract void showOptions();

    // Abstract method for handling user input (command execution)
    public abstract void handleInput(String command);

    // Method to start the user interface and keep listening for input until the user exits
    public void start() {
        String command;
        showOptions();  // Show available options/commands to the user

        while (true) {
            System.out.print("Enter command: ");
            command = scanner.nextLine().trim().toLowerCase();

            if (command.equals("exit")) {
                System.out.println("Exiting...");
                break;
            }

            handleInput(command);  // Process the command entered by the user
        }
    }

    // Utility method to display an error message if the user enters an invalid command
    protected void displayError(String errorMessage) {
        System.out.println("Error: " + errorMessage);
        System.out.println("Type 'help' to see available commands.");
    }

    // Utility method to pause and wait for user to press Enter before continuing
    protected void waitForEnter() {
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }

    // Utility method to show the help message with all available commands
    public void showHelp() {
        System.out.println("Available commands:");
        System.out.println("help - Show options");
        System.out.println("exit - Exit the application");
        System.out.println("Type the command and press Enter.");
    }

}
