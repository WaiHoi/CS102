package cardgame;

import java.io.*;
import java.net.*;
import java.util.*;

import cardgame.*;
import cardgame.game.*;
import cardgame.network.*;
import cardgame.utility.*;


import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

public class GameMenu {

    private String username;
    public static int numPlayers;
    public static int numBots;

    public void displayMainMenu() {
        System.out.println("\n====Welcome to a game of Parades!====");
        System.out.println("Enjoy and have fun!");
        System.out.println("Type /help for commands");
        System.out.println("=====================================");
        System.out.println("1. Host & Play (Start Server + Client)");
        System.out.println("2. Join Existing Game (Client Only)");
        System.out.println("3. Play Locally (Console Mode)");
        System.out.println("4. Exit\n");
        System.out.print("Choose an option: ");
    }

    public void displayPlayerSetup() {
        numPlayers = 0;
        numBots = 0;
        System.out.println(
            ansi().fgBrightCyan().a("Welcome ")
            .fgBrightYellow().a("to a ")
            .fgBrightMagenta().a("game ")
            .fgBrightGreen().a("of ")
            .fgBrightRed().a("Parades!\n")
            .fgBrightBlue().a("Enjoy ")
            .fgBrightYellow().a("and ")
            .fgBrightCyan().a("have fun!")
            .reset()
        );

        while (numPlayers + numBots < 2 || numPlayers + numBots > 6) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print(ansi().fgBrightCyan().a("Enter number of human players: ").reset());
                numPlayers = sc.nextInt();
                sc.nextLine();

                if (numPlayers < 0 || numPlayers > 6) {
                    throw new InputMismatchException();
                }

                System.out.print(ansi().fgBrightMagenta().a("Enter number of bot players: ").reset());
                numBots = sc.nextInt();
                sc.nextLine();

                if (numBots < 0 || numBots > 6) {
                    throw new InputMismatchException();
                }

                if (numPlayers + numBots < 2 || numPlayers + numBots > 6) {
                    System.out.println(ansi().fgYellow().a("Total number of players and bots should be between 2 and 6!\n").reset());                
                }
            } catch (InputMismatchException e) {
                System.out.println(ansi().fgRed().a("Input must be a positive integer and not more than 6!\n").reset());            
            }
            
        }
    }

    public void readOption() {
        boolean isRunning = true;
        Scanner sc = new Scanner(System.in);

        while (isRunning) {
            displayMainMenu();
            
            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        hostAndPlay();
                        // exit menu after selection
                        isRunning = false;
                        break;
                    case 2:
                        joinGame();
                        isRunning = false;
                        break;
                    case 3:
                        startLocalGame();
                        isRunning = false;
                        break;
                    case 4:
                        System.out.println("Exiting game...");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please enter 1-4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number between 1-4!");
                sc.nextLine();
            }
        }
    }

    private List<String> getPlayerNames(int numPlayers) {
        List<String> usernames = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < numPlayers; i++) {

            while (true) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                String name = sc.nextLine().strip();
    
                if (UsernameValidator.checkUsername(name)) {
                    usernames.add(name);
                    break;
                } else {
                    System.out.println("Username '" + name + "' is taken. Try another.");
                }
            }
        }
        return usernames;

    }

    public void hostAndPlay() {
        displayPlayerSetup();

        new Thread(() -> {
            try {
                // start server
                ParadeServer.main(new String[0]); 

            } catch (IOException e) {
                System.out.println("Failed to connect to server. Starting a local game instead.");
                startLocalGame();
            }
        }).start();

        System.out.println("[SERVER] Starting server on port 1234");

        // 2 second buffer to wait for server to start
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("[SERVER] Waiting for connections...\n");
        connectToGame("localhost");

    }

    private void joinGame() {
        connectToGame("localhost");

    }

    private void startLocalGame() {
        displayPlayerSetup();

        // unique usernames 
        List<String> playerNames = getPlayerNames(numPlayers);

        // bots 
        for (int i = 0; i < numBots; i++) {
            playerNames.add("Bot " + (i++));
        }

        Initialize.initializeVariables();
        Game.mainFunction();

    }

    private void connectToGame(String host) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username: ");
        this.username = sc.nextLine();

        try {
            System.out.println("\nConnecting to the server");
            Socket socket = new Socket(host, 1234);
            ParadeClient client = new ParadeClient(socket, username);
            client.startClient();
            
        } catch (IOException e) {
            System.out.println("Failed to connect to server. Starting a local game instead.");
            startLocalGame();
        }
    }

    public static void main(String[] args) {

        AnsiConsole.systemInstall();

        GameMenu menu = new GameMenu();
        menu.readOption();

        // Initialize.initializeVariables();
        // Game.mainFunction();

        AnsiConsole.systemUninstall();
        // Score score = new Score();
        // score.isWinner(g.players, g);
    }

}