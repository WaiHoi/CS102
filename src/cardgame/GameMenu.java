// package cardgame;

// import java.util.*;

// import cardgame.game.*;
// import cardgame.model.*;
// import cardgame.utility.*;

// import org.fusesource.jansi.AnsiConsole;
// import static org.fusesource.jansi.Ansi.DISABLE;
// import static org.fusesource.jansi.Ansi.ansi;


// public class GameMenu {

//     private static final Scanner sc = new Scanner(System.in);
//     public static String username;
//     public static List<String> usernames = new ArrayList<>();
//     public static int numHumans;
//     public static int numBots;
//     public static BotDifficulty botDifficulty;
//     public static final int consoleWidth = 40;
    

//     // Display the main menu
//     public static void displayMainMenu() {
//         printBorder();
//         System.out.println(AnsiColors.colorize("Welcome to a game of ", AnsiColors.BRIGHT_CYAN) +
//                 AnsiColors.colorize("Parades!", AnsiColors.BRIGHT_MAGENTA + AnsiColors.BOLD));
//         System.out.println(AnsiColors.colorizeBold("ENJOY AND HAVE FUN!", AnsiColors.BRIGHT_YELLOW));
//         printBorder();

//         System.out.println(AnsiColors.colorize("[1] Play Locally (Console Mode)", AnsiColors.BRIGHT_CYAN));
//         System.out.println(AnsiColors.colorize("[2] Exit", AnsiColors.BRIGHT_RED));
//         printBorder();
//     }
    
    


//     public static void displayPlayerSetup() {
//         numHumans = 0;
//         numBots = 0;

//         System.out.println("\n+--------------------------------------+");
//         System.out.println("|     Choose Number of Players         |");
//         System.out.println("+--------------------------------------+");


//         while (numHumans + numBots < 2 || numHumans + numBots > 6) {
//             // human players input
//             while (true) {
//                 try {
//                     System.out.print(ansi().fgBrightCyan().a("Enter number of human players: ").reset());
//                     numHumans = sc.nextInt();
//                     sc.nextLine();

//                     if (numHumans >= 0 && numHumans <= 6) {
//                         break;
//                     } else {
//                         System.out.println(ansi().fgRed().a("Number must be between 0 and 6!\n").reset());
//                     }

//                 } catch (InputMismatchException e) {
//                     System.out.println(ansi().fgRed().a(
//                         "Input must be a positive integer and not more than 6!\n").reset());
//                     sc.nextLine();
//                 }
//             }

//             while (true) {
//                 try {
//                     System.out.print(ansi().fgBrightMagenta().a("Enter number of bot players: ").reset());
//                     numBots = sc.nextInt();
//                     sc.nextLine();

//                     if (numBots >= 0 && numBots <= 6) {
//                         break;
//                     } else {
//                         System.out.println(ansi().fgRed().a("Number must be between 0 and 6!\n").reset());
//                     }

//                 } catch (InputMismatchException e) {
//                     System.out.println(ansi().fgRed().a(
//                         "Input must be a positive integer and not more than 6!\n").reset());
//                     sc.nextLine();
//                 }
//             }

//             if (numHumans + numBots < 2 || numHumans + numBots > 6) {
//                 System.out.println(ansi().fgYellow().a(
//                     "Total number of players and bots should be between 2 and 6!\n").reset()); 
//             }
//         }
//     }

//     public static BotDifficulty setBotDifficulty() {
//         BotDifficulty difficulty = null;

//         System.out.println("\n+--------------------------------------+");
//         System.out.println("|      Select bot difficulty level     |");
//         System.out.println("+--------------------------------------+");
//         System.out.println("[1] Easy");
//         System.out.println("[2] Medium");
//         System.out.println("[3] Hard");
//         System.out.println("----------------------------------------");

//         while (difficulty == null) {
//             System.out.print("Enter your choice: ");

//             try {
//                 int choice = sc.nextInt();
//                 sc.nextLine();

//                 switch (choice) {
//                     case 1:
//                         difficulty = BotDifficulty.EASY;
//                         break;
//                     case 2:
//                         difficulty = BotDifficulty.MEDIUM;
//                         break;
//                     case 3:
//                         difficulty = BotDifficulty.HARD;
//                         break;
//                     default:
//                         System.out.println("Invalid choice. Please enter a number between 1 to 3.");
//                 }
//             } catch (InputMismatchException e) {
//                 System.out.println("Please enter a number between 1 to 3!");
//                 sc.nextLine();
//             }
//         }
//         return difficulty;
//     }

//     public static void startGame() {
//         displayMainMenu();

//         while (true) {
//             System.out.print("Enter a number: ");
            
//             try {
//                 int choice = sc.nextInt();
//                 sc.nextLine();

//                 switch (choice) {
//                     case 1:
//                         startLocalGame();
//                         return;
//                     case 2:
//                         System.out.println("Exiting game...");
//                         System.exit(0);
//                     default:
//                         System.out.println("Invalid choice. Please enter either 1 or 2.");
//                 }
//             } catch (InputMismatchException e) {
//                 System.out.println("Invalid choice. Please enter either 1 or 2.");
//                 sc.nextLine();
//             }
//         }

//     }

//     private static List<String> getPlayerNames(int numPlayers) {
//         List<String> usernames = new ArrayList<>();
//         StringBuilder errorMsg = new StringBuilder();
//         System.out.println("\n+--------------------------------------+");
//         System.out.println("|        Enter Names of Players        |");
//         System.out.println("+--------------------------------------+");

//         for (int i = 0; i < numPlayers; i++) {

//             while (true) {
//                 System.out.print("Enter name for Player " + (i + 1) + ": ");
//                 String name = sc.nextLine().trim();

//                 if (UsernameValidator.validateUsername(name, errorMsg)) {
//                     usernames.add("Player " + name);
//                     break;
//                 }
//                 System.out.println(errorMsg);
//             }
//         }
//         return usernames;
//     }

//     private static void initializeGame() {
//         TurnManager.initialize(false, numHumans, numBots);
//         Initialize.initializeVariables(usernames, numHumans, numBots, botDifficulty);

//         displayGameState();
//         Game.mainFunction(false);
//     }

//     private static void setupBots() {
//         // set bot difficulty
//         if (numBots > 0) {
//             botDifficulty = setBotDifficulty();
//         }
//     }

//     private static void setupPlayers() {
//         displayPlayerSetup();
//         Player.players.clear();
//         usernames = getPlayerNames(numHumans);
//     }

//     public static void startLocalGame() {
//         setupPlayers();
//         setupBots();
//         initializeGame();
//         startGame();

//         // displayPlayerSetup();
//         // Player.players.clear();

//         // // unique usernames 
//         // usernames = getPlayerNames(numHumans);

//         // // set bot difficulty
//         // if (numBots > 0) {
//         //     botDifficulty = setBotDifficulty();
//         // }

//         // TurnManager.initialize(false, numHumans, numBots);
//         // Initialize.initializeVariables(usernames, numHumans, numBots, botDifficulty);

//         // displayGameState();
//         // Game.mainFunction(false);
//     }

//     public static void displayGameState() {
//         clearConsole();

//         System.out.println("\n+--------------------------------------+");
//         System.out.println("|                                      |");
//         System.out.println("|          GAME HAS STARTED!           |");
//         System.out.println("|                                      |");
//         System.out.println("+--------------------------------------+");

//         System.out.println("\n Players: ");
        
//         for (int i = 0; i < Player.players.size(); i++) {
//             Player player = Player.players.get(i);

//             if (player instanceof Human) {
//                 System.out.println("   " + (i + 1) + ". " + player.getPlayerName() + " (HUMAN)");
//             }

//             if (player instanceof Bot) {
//                 System.out.println("   " + (i + 1) + ". " + player.getPlayerName() + " (" +
//                                     ((Bot) player).getDifficulty() + ")");
//             }
//         }
        
//         System.out.println("\n Total Players: " + Player.players.size());
//         System.out.println("----------------------------------------");
//     }

//     public static void clearConsole() {
//         System.out.print("\033[H\033[2J");
//         System.out.flush();
//     }

//     public static void main(String[] args) {

//         AnsiConsole.systemInstall();

//         startGame();

//         AnsiConsole.systemUninstall();
//     }

// }
package cardgame;

import java.util.*;
import cardgame.game.*;
import cardgame.model.*;
import cardgame.utility.*;

import org.fusesource.jansi.AnsiConsole;

public class GameMenu {

    private static final Scanner scanner = new Scanner(System.in);
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;
    private static final int CONSOLE_WIDTH = 40;

    private static String username;
    private static List<String> usernames = new ArrayList<>();
    private static int numHumans;
    private static int numBots;
    private static BotDifficulty botDifficulty;

    // Display the main menu
    public static void displayMainMenu() {
        printBorder();
        System.out.println(AnsiColors.colorize("Welcome to a game of ", AnsiColors.BRIGHT_CYAN) +
                AnsiColors.colorize("Parades!", AnsiColors.BRIGHT_MAGENTA + AnsiColors.BOLD));
        System.out.println(AnsiColors.colorizeBold("ENJOY AND HAVE FUN!", AnsiColors.BRIGHT_YELLOW));
        printBorder();

        System.out.println(AnsiColors.colorize("[1] Play Locally (Console Mode)", AnsiColors.BRIGHT_CYAN));
        System.out.println(AnsiColors.colorize("[2] Exit", AnsiColors.BRIGHT_RED));
        printBorder();
    }

    // Display player setup menu
    public static void displayPlayerSetup() {
        numHumans = 0;
        numBots = 0;

        printHeader("Choose Number of Players");

        while (!isValidPlayerCount(numHumans, numBots)) {
            numHumans = getValidatedInput("Enter number of human players: ", 0, MAX_PLAYERS);
            numBots = getValidatedInput("Enter number of bot players: ", 0, MAX_PLAYERS);

            if (!isValidPlayerCount(numHumans, numBots)) {
                System.out.println(AnsiColors.colorizeBold(
                        "Total number of players must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS + "!\n",
                        AnsiColors.BRIGHT_YELLOW));
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
                String name = scanner.nextLine().trim();

                if (UsernameValidator.validateUsername(name, errorMsg)) {
                    usernames.add("Player " + name);
                    break;
                }
                System.out.println(errorMsg);
            }
        }
        return usernames;
    }

    // Validate player count
    private static boolean isValidPlayerCount(int humans, int bots) {
        int totalPlayers = humans + bots;
        return totalPlayers >= MIN_PLAYERS && totalPlayers <= MAX_PLAYERS;
    }

    // Get validated input from the user
    private static int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(AnsiColors.colorize(prompt, AnsiColors.BRIGHT_CYAN));
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println(AnsiColors.colorizeBold(
                            "Number must be between " + min + " and " + max + "!\n", AnsiColors.BRIGHT_RED));
                }
            } catch (InputMismatchException e) {
                System.out.println(AnsiColors.colorizeBold(
                        "Input must be a positive integer!\n", AnsiColors.BRIGHT_RED));
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Set bot difficulty
    public static BotDifficulty setBotDifficulty() {
        printHeader("Select Bot Difficulty Level");
        System.out.println("[1] Easy\n[2] Medium\n[3] Hard");
        printBorder();

        while (true) {
            try {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1: return BotDifficulty.EASY;
                    case 2: return BotDifficulty.MEDIUM;
                    case 3: return BotDifficulty.HARD;
                    default:
                        System.out.println(AnsiColors.colorizeBold(
                                "Invalid choice! Please enter a number between 1 and 3.", AnsiColors.BRIGHT_RED));
                }
            } catch (InputMismatchException e) {
                System.out.println(AnsiColors.colorizeBold(
                        "Please enter a valid number between 1 and 3!", AnsiColors.BRIGHT_RED));
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Start the game
    public static void startGame() {
        displayMainMenu();

        while (true) {
            try {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        startLocalGame();
                        return;
                    case 2:
                        System.out.println("Exiting game...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println(AnsiColors.colorizeBold(
                                "Invalid choice! Please enter either 1 or 2.", AnsiColors.BRIGHT_RED));
                }
            } catch (InputMismatchException e) {
                System.out.println(AnsiColors.colorizeBold(
                        "Invalid input! Please enter either 1 or 2.", AnsiColors.BRIGHT_RED));
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    // Start local game setup
    public static void startLocalGame() {
        setupHumans();
        setupBots();
        initializeGame();
        startGame();
    }

    // Setup players
    private static void setupHumans() {
        displayPlayerSetup();
        Player.players.clear();
        usernames = getPlayerNames(numHumans);
    }

    // Setup bots
    private static void setupBots() {
        if (numBots > 0) {
            botDifficulty = setBotDifficulty();
        }
    }

    // Initialize the game
    private static void initializeGame() {
        TurnManager.initialize(false, numHumans, numBots);
        Initialize.initializeVariables(usernames, numHumans, numBots, botDifficulty);

        displayGameState();
        Game.mainFunction(false);
    }

    // Display game state
    public static void displayGameState() {
        clearConsole();

        printHeader("GAME HAS STARTED!");

        System.out.println("\nPlayers:");
        
        for (int i = 0; i < Player.players.size(); i++) {
            Player player = Player.players.get(i);

            if (player instanceof Human) {
                System.out.println("   " + (i + 1) + ". " + player.getPlayerName() + " (HUMAN)");
            } else if (player instanceof Bot) {
                System.out.println("   " + (i + 1) + ". " + player.getPlayerName() +
                        " (" + ((Bot) player).getDifficulty() + ")");
            }
        }

        System.out.println("\nTotal Players: " + Player.players.size());
        printBorder();
    }

    // Clear console output
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Print header with borders
    private static void printHeader(String header) {
        printBorder();
        System.out.printf("|%s|\n", centerText(header, CONSOLE_WIDTH));
        printBorder();
    }

    // Print border line
    private static void printBorder() {
        System.out.println("+--------------------------------------+");
    }

    // Center text within a fixed width
    private static String centerText(String text, int width) {
        int padding = Math.max((width - text.length()) / 2, 0);
        return String.format("%" + padding + "s%s%" + padding + "s", "", text, "");
    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        
        startGame();

        AnsiConsole.systemUninstall();
    }
}
