package cardgame;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import cardgame.*;
import cardgame.game.*;
import cardgame.model.*;
import cardgame.utility.*;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

public class GameMenu {

    private static final int serverPort = 1234;
    // create new thread
    private static Thread serverThread;
    // whether server is ready
    private static final AtomicBoolean serverReady = new AtomicBoolean(false);  
    // reading input
    private static final Scanner sc = new Scanner(System.in);

    public static String username;
    public static List<String> usernames = new ArrayList<>();
    public static int numHumans;
    public static int numBots;
    
    public static void displayMainMenu() {
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
        System.out.println(ansi().fgBrightGreen().a("Type /help for commands"));
        System.out.println("=====================================");
        System.out.println(ansi().fgBrightCyan().a("1. Play Locally (Console Mode)"));
        System.out.println(ansi().fgBrightMagenta().a("2. Exit"));
        System.out.print("Choose an option: ");
    }

    public static void displayPlayerSetup() {
        numHumans = 0;
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

        while (numHumans + numBots < 2 || numHumans + numBots > 6) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print(ansi().fgBrightCyan().a("Enter number of human players: ").reset());
                numHumans = sc.nextInt();
                sc.nextLine();

                if (numHumans < 0 || numHumans > 6) {
                    throw new InputMismatchException();
                }

                System.out.print(ansi().fgBrightMagenta().a("Enter number of bot players: ").reset());
                numBots = sc.nextInt();
                sc.nextLine();

                if (numBots < 0 || numBots > 6) {
                    throw new InputMismatchException();
                }

                if (numHumans + numBots < 2 || numHumans + numBots > 6) {
                    System.out.println(ansi().fgYellow().a("Total number of players and bots should be between 2 and 6!\n").reset());                
                }
            } catch (InputMismatchException e) {
                System.out.println(ansi().fgRed().a("Input must be a positive integer and not more than 6!\n").reset());            
            }
            
        }
    }

    public static void startGame() {

        while (true) {
            displayMainMenu();
            
            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        startLocalGame();
                        return;
                    case 2:
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

    private static List<String> getPlayerNames(int numPlayers) {
        Scanner sc = new Scanner(System.in);
        List<String> usernames = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();


        for (int i = 0; i < numPlayers; i++) {

            while (true) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                String name = sc.nextLine().trim();

                if (UsernameValidator.validateUsername(name, errorMsg)) {
                    usernames.add("Player " + name);
                    break;
                }
                System.out.println(errorMsg);
            }
        }
        return usernames;

    }

    public static void startLocalGame() {
        
        displayPlayerSetup();
        Player.players.clear();

        // unique usernames 
        usernames = getPlayerNames(numHumans);

        TurnManager.initialize(false, numHumans, numBots);

        Initialize.initializeVariables(usernames, numHumans, numBots);
        Game.mainFunction(false);
        Score.calculateScore();

    }

    public static void main(String[] args) {

        AnsiConsole.systemInstall();

        startGame();

        AnsiConsole.systemUninstall();
    }

}