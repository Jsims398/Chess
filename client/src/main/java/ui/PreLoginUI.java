package ui;

import facade.ServerFacade;

public class PreLoginUI extends UserInterface{
    public PreLoginUI(ServerFacade facade){
        super(facade);
    }

    @Override
    public void showOptions() {
        System.out.println("Available commands:");
        System.out.println("help - Show options");
        System.out.println("quit - Exit");
        System.out.println("login - Login");
        System.out.println("register - Register");
    }

    @Override
    public void handleInput(String command) {
        switch (command) {
            case "login":

                break;
            case "register":

                break;
            case "quit":
                System.exit(0);
                break;
            default:
                displayError("Unknown command.");
        }
    }
}
