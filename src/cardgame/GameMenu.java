package cardgame;

import java.util.*;

import cardgame.game.*;
import cardgame.model.*;
import cardgame.utility.*;

import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.DISABLE;
import static org.fusesource.jansi.Ansi.ansi;

public class GameMenu {

    private static final Scanner sc = new Scanner(System.in);
    public static String username;
    public static List<String> usernames = new ArrayList<>();
    public static int numHumans;
    public static int numBots;
    public static BotDifficulty botDifficulty;
    public static final int consoleWidth = 40;
    
    public static void displayMainMenu() {
        System.out.println("\n+--------------------------------------+");
        System.out.println("|                                      |");
        System.out.println("|" +
            ansi().fgBrightCyan().a("   Welcome ")
            .fgBrightYellow().a("to a ")
            .fgBrightMagenta().a("game ")
            .fgBrightGreen().a("of ")
            .fgBrightRed().a("Parades!      ").reset() + "|"
        );
        System.out.println("|" +
            ansi().fgBrightBlue().a("        Enjoy ")
            .fgBrightYellow().a("and ")
            .fgBrightCyan().a("have fun!           ")
            .reset() + "|"
        );
        System.out.println("|                                      |");
        System.out.println("+--------------------------------------+");
        System.out.println(ansi().fgBrightCyan().a("[1] Play Locally (Console Mode)"));
        System.out.println(ansi().fgBrightMagenta().a("[2] Exit").reset());
        System.out.println("----------------------------------------");

    }


    public static void displayPlayerSetup() {
        numHumans = 0;
        numBots = 0;

        System.out.println("\n+--------------------------------------+");
        System.out.println("|     Choose Number of Players         |");
        System.out.println("+--------------------------------------+");


        while (numHumans + numBots < 2 || numHumans + numBots > 6) {
            // human players input
            while (true) {
                try {
                    System.out.print(ansi().fgBrightCyan().a("Enter number of human players: ").reset());
                    numHumans = sc.nextInt();
                    sc.nextLine();

                    if (numHumans >= 0 && numHumans <= 6) {
                        break;
                    } else {
                        System.out.println(ansi().fgRed().a("Number must be between 0 and 6!\n").reset());
                    }

                } catch (InputMismatchException e) {
                    System.out.println(ansi().fgRed().a(
                        "Input must be a positive integer and not more than 6!\n").reset());
                    sc.nextLine();
                }
            }

            while (true) {
                try {
                    System.out.print(ansi().fgBrightMagenta().a("Enter number of bot players: ").reset());
                    numBots = sc.nextInt();
                    sc.nextLine();

                    if (numBots >= 0 && numBots <= 6) {
                        break;
                    } else {
                        System.out.println(ansi().fgRed().a("Number must be between 0 and 6!\n").reset());
                    }

                } catch (InputMismatchException e) {
                    System.out.println(ansi().fgRed().a(
                        "Input must be a positive integer and not more than 6!\n").reset());
                    sc.nextLine();
                }
            }

            if (numHumans + numBots < 2 || numHumans + numBots > 6) {
                System.out.println(ansi().fgYellow().a(
                    "Total number of players and bots should be between 2 and 6!\n").reset()); 
            }
        }
    }

    public static BotDifficulty setBotDifficulty() {
        BotDifficulty difficulty = null;

        System.out.println("\n+--------------------------------------+");
        System.out.println("|      Select bot difficulty level     |");
        System.out.println("+--------------------------------------+");
        System.out.println("[1] Easy");
        System.out.println("[2] Medium");
        System.out.println("[3] Hard");
        System.out.println("----------------------------------------");

        while (difficulty == null) {
            System.out.print("Enter your choice: ");

            try {
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1:
                        difficulty = BotDifficulty.EASY;
                        break;
                    case 2:
                        difficulty = BotDifficulty.MEDIUM;
                        break;
                    case 3:
                        difficulty = BotDifficulty.HARD;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 to 3.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a number between 1 to 3!");
                sc.nextLine();
            }
        }
        return difficulty;
    }

    public static void startGame() {
        displayMainMenu();

        while (true) {
            System.out.print("Enter a number: ");
            
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
                        System.out.println("Invalid choice. Please enter either 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid choice. Please enter either 1 or 2.");
                sc.nextLine();
            }
        }

    }

    private static List<String> getPlayerNames(int numPlayers) {
        List<String> usernames = new ArrayList<>();
        StringBuilder errorMsg = new StringBuilder();
        System.out.println("\n+--------------------------------------+");
        System.out.println("|        Enter Names of Players        |");
        System.out.println("+--------------------------------------+");

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

    private static void initializeGame() {
        TurnManager.initialize(false, numHumans, numBots);
        Initialize.initializeVariables(usernames, numHumans, numBots, botDifficulty);

        displayGameState();
        Game.mainFunction(false);
    }

    private static void setupBots() {
        // set bot difficulty
        if (numBots > 0) {
            botDifficulty = setBotDifficulty();
        }
    }

    private static void setupPlayers() {
        displayPlayerSetup();
        Player.players.clear();
        usernames = getPlayerNames(numHumans);
    }

    public static void startLocalGame() {
        setupPlayers();
        setupBots();
        initializeGame();
        startGame();

        // displayPlayerSetup();
        // Player.players.clear();

        // // unique usernames 
        // usernames = getPlayerNames(numHumans);

        // // set bot difficulty
        // if (numBots > 0) {
        //     botDifficulty = setBotDifficulty();
        // }

        // TurnManager.initialize(false, numHumans, numBots);
        // Initialize.initializeVariables(usernames, numHumans, numBots, botDifficulty);

        // displayGameState();
        // Game.mainFunction(false);
    }

    public static void displayGameState() {
        clearConsole();

        System.out.println("\n+--------------------------------------+");
        System.out.println("|                                      |");
        System.out.println("|          GAME HAS STARTED!           |");
        System.out.println("|                                      |");
        System.out.println("+--------------------------------------+");

        System.out.println("\n Players: ");
        
        for (int i = 0; i < Player.players.size(); i++) {
            Player player = Player.players.get(i);

            if (player instanceof Human) {
                System.out.println("   " + (i + 1) + ". " + player.getPlayerName() + " (HUMAN)");
            }

            if (player instanceof Bot) {
                System.out.println("   " + (i + 1) + ". " + player.getPlayerName() + " (" +
                                    ((Bot) player).getDifficulty() + ")");
            }
        }
        
        System.out.println("\n Total Players: " + Player.players.size());
        System.out.println("----------------------------------------");
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public static void main(String[] args) {

        AnsiConsole.systemInstall();

        startGame();

        AnsiConsole.systemUninstall();
    }

}