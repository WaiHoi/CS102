package cardgame;

import java.util.*;
import cardgame.game.*;
import cardgame.model.*;
import cardgame.utility.*;

//java.lang.*; is automatically done by java complier for every single java file
/* Automatically done behind the scenes:
import java.lang.System;
import java.lang.String;
import java.lang.Math;
... and many more*/

import org.fusesource.jansi.AnsiConsole;

import com.github.lalyos.jfiglet.FigletFont;


public class GameMenu {

    private static final Scanner scanner = new Scanner(System.in);

    //Minimum and Maximum number of total players allowed 
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;

    private static final int CONSOLE_WIDTH = 40;

    //Variables for storing user and bot data
    private static String username;
    private static List<String> usernames = new ArrayList<>();
    private static int numHumans;
    private static int numBots;
    private static BotDifficulty botDifficulty;


    public static void displayMainMenu() {
    
        try {
            // Generate big bold ASCII art for "Parades"
            String asciiArt = FigletFont.convertOneLine("Parades");
    
            // Apply colors to the ASCII art
            String coloredAsciiArt = AnsiColors.colorize(asciiArt, AnsiColors.BRIGHT_MAGENTA + AnsiColors.BOLD);
    
            // Display the colored ASCII art
            System.out.println(coloredAsciiArt);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }
    
        // Add additional menu options with colors
        System.out.println(AnsiColors.colorize("[1] Play Locally (Console Mode)", AnsiColors.BRIGHT_CYAN));
        System.out.println(AnsiColors.colorize("[2] Exit", AnsiColors.BRIGHT_RED));
    
    }

    public static void displayPlayerSetup() {
        numHumans = 0; // Initialize numHumans as 0
        numBots = 0; // Initialize numBots as 0
    
        // Generate Figlet-style header for "Choose Number of Players"
        try {
            String headerAsciiArt = FigletFont.convertOneLine("Choose Players");
            System.out.println(AnsiColors.colorize(headerAsciiArt, AnsiColors.BRIGHT_MAGENTA + AnsiColors.BOLD));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }
    
        // Keep asking until valid player count is entered
        while (!isValidPlayerCount(numHumans, numBots)) {
            // Prompt for number of human players
            numHumans = getValidatedInput(
                AnsiColors.colorize("Enter number of human players: ", AnsiColors.BRIGHT_CYAN),
                0, MAX_PLAYERS
            );
    
            // Prompt for number of bot players
            numBots = getValidatedInput(
                AnsiColors.colorize("Enter number of bot players: ", AnsiColors.BRIGHT_CYAN),
                0, MAX_PLAYERS
            );
    
            // Warning if total number of players is not within limits
            if (!isValidPlayerCount(numHumans, numBots)) {
                System.out.println(AnsiColors.colorizeBold(
                    "Total number of players must be between " + MIN_PLAYERS + " and " + MAX_PLAYERS + "!\n",
                    AnsiColors.BRIGHT_RED
                ));
            }
        }
    }

    private static List<String> getPlayerNames(int numPlayers) {

        // clear usernames
        usernames.clear();

        StringBuilder errorMsg = new StringBuilder(); // Used to capture why a username is invalid
    
        // Generate Figlet-style header for "Enter Names of Players"
        try {
            String headerAsciiArt = FigletFont.convertOneLine("Enter Names");
            System.out.println(AnsiColors.colorize(headerAsciiArt, AnsiColors.BRIGHT_CYAN + AnsiColors.BOLD));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }
    
        // Prompt for player names
        for (int i = 0; i < numPlayers; i++) {
            while (true) {
                System.out.print(AnsiColors.colorize("Enter name for Player " + (i + 1) + ": ", AnsiColors.BRIGHT_YELLOW));
                String name = scanner.nextLine().trim();
    
                // Use validator to check for errors in the name
                if (UsernameValidator.validateUsername(name, errorMsg)) {
                    usernames.add("Player " + name); // Prefix the name with "Player"
                    break;
                }
    
                // Display error message in red
                System.out.println(AnsiColors.colorize(errorMsg.toString(), AnsiColors.BRIGHT_RED));
            }
        }
    
        return usernames;
    }

    // Check if total players are within the acceptable limits
    private static boolean isValidPlayerCount(int humans, int bots) {
        int totalPlayers = humans + bots;
        return totalPlayers >= MIN_PLAYERS && totalPlayers <= MAX_PLAYERS; 
        //MIN_PLAYER is initialized as 2 and MAX_PLAYER is initialized as 6. 
        //we need at least 2 players to play the game and we cannot have more than 6 players
        //returns true if number of players is between 2 to 6 (inclusive)
    }

    // Get validated input from the user
    private static int getValidatedInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(AnsiColors.colorize(prompt, AnsiColors.BRIGHT_CYAN));
                int input = scanner.nextInt();
                scanner.nextLine(); 

                if (input >= min && input <= max) {
                    return input;
                } else {
                    System.out.println(AnsiColors.colorizeBold(
                            "Number must be between " + min + " and " + max + "!\n", AnsiColors.BRIGHT_RED));
                }
            } catch (InputMismatchException e) {
                //Catches invalid input
                System.out.println(AnsiColors.colorizeBold(
                        "Input must be a positive integer!\n", AnsiColors.BRIGHT_RED));
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    public static BotDifficulty setBotDifficulty() {
        // Generate Figlet-style header for "Select Bot Difficulty Level"
        try {
            String headerAsciiArt = FigletFont.convertOneLine("Bot Difficulty");
            System.out.println(AnsiColors.colorize(headerAsciiArt, AnsiColors.BRIGHT_CYAN + AnsiColors.BOLD));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }
    
        // Display difficulty options with colors
        System.out.println(AnsiColors.colorize("[1] Easy", AnsiColors.BRIGHT_GREEN));
        System.out.println(AnsiColors.colorize("[2] Medium", AnsiColors.BRIGHT_YELLOW));
        System.out.println(AnsiColors.colorize("[3] Hard", AnsiColors.BRIGHT_RED));
    
        while (true) {
            try {
                System.out.print(AnsiColors.colorize("Enter your choice: ", AnsiColors.BRIGHT_MAGENTA));
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
    
                switch (choice) {
                    case 1:
                        return BotDifficulty.EASY;
                    case 2:
                        return BotDifficulty.MEDIUM;
                    case 3:
                        return BotDifficulty.HARD;
                    default: // If user keys in an int that is not 1, 2, or 3
                        System.out.println(AnsiColors.colorizeBold(
                                "Invalid choice! Please enter a number between 1 and 3.", AnsiColors.BRIGHT_RED));
                }
            } catch (InputMismatchException e) {
                // Handles non-integer input
                System.out.println(AnsiColors.colorizeBold(
                        "Please enter a valid number between 1 and 3!", AnsiColors.BRIGHT_RED));
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
    

    // Start the game
    public static void startGame() {
        displayMainMenu(); // call displayMainMenu from GameMenu class (line 308)

        while (true) {
            //Starts an infinite loop that keeps running until you explicitly return or System.exit(0);.
            //This is used to keep asking the player for valid input choice = 1 or choice = 2, until they give it.
            try {
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt(); 
                //scanner.nextInt() Reads just the number, 
                //Leaves the newline \n (the Enter key) in the input buffer
                scanner.nextLine(); 
                //Sees the leftover \n sitting there
                //Reads everything up to and including the \n
                //Returns an empty string ("") because there was nothing typed before the newline
                //returns to nothing because its not assigned to a variable
                //Clears the buffer
                //note: without scanner.nextLine() above any following nextLine() would immediately return an empty string
                //because it would just read the leftover \n instead of waiting for actual input.

                switch (choice) {//switch case
                    case 1:
                        startLocalGame(); 
                        return; //exits the start game method completely, no return variable type as its a void method
                    case 2:
                        System.out.println("Exiting game...");
                        System.exit(0);// teminates the program by calling the static exit method from System
                    default:  // Handles cases where the user enters an integer other than 1 or 2
                        System.out.println(AnsiColors.colorizeBold(
                                "Invalid choice! Please enter either 1 or 2.", AnsiColors.BRIGHT_RED)); 
                                //explanation for this mentioned in displayMainMenu() method
                                //no need for scanner.nextLine() as the user has already entered an integer, so 
                                //the newline has already been cleared by the scanner.nextLine() right after it (in line 455)
                }
            } catch (InputMismatchException e) { //
                //Handle invalid cases when the input, choice, is not a integer
                System.out.println(AnsiColors.colorizeBold(
                        "Invalid input! Please enter either 1 or 2.", AnsiColors.BRIGHT_RED)); // explanation for this is in displayMainMenu() method
                scanner.nextLine(); //clears the line by returning the invalid String input. its invalid because int is the required datatype, not String.
            }
        }
    }

    // Start local game setup
    public static void startLocalGame() {
        setupHumans(); //select the number of humans and bots, 2 <= number of players <= 6
        setupBots();  //sets up bot difficulty (from level 1 to 3) if numBots > 0, bot difficulty is the same for all bots
        initializeGame(); 
        //resets the game, adds all Bots and humans to players, 
        //shuffles the deck, deals the card to players and puts the following 6 cards into Parade, 
        //and displays game state, handles the loop of the game
        startGame(); 
    }

    // Setup players
    private static void setupHumans() {
    // Collect user input and player names
        displayPlayerSetup(); 
        usernames = getPlayerNames(numHumans); //usernames is of type List<String>, getPlayerNames() in line 87
    }

    // Ask for difficulty level if bots are present
    private static void setupBots() {
        if (numBots > 0) {
            botDifficulty = setBotDifficulty(); 
            //how bot difficulty affects how the cards are being selected for bot is defined in Bot.java
        }
    }

    // Initialize the game
    private static void initializeGame() {

        // reset game 
        Game.resetGame();
        Initialize.initializeVariables(usernames, numHumans, numBots, botDifficulty);

        displayGameState();
        Game.mainFunction();
    }

    // Display game state with all players listed
    public static void displayGameState() {
        clearConsole();

        printHeader("GAME HAS STARTED!");

        System.out.println("\nPlayers:");
        
        //List all players and whether they are human or not
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
        System.out.printf("|%s|\n", centerText(header, CONSOLE_WIDTH)); // centres the header
        printBorder();
    }

    // Print border line
    private static void printBorder() {
        System.out.println("+--------------------------------------+");
    }

    // Center text within a fixed width
    private static String centerText(String text, int width) {
        int padding = Math.max((width - text.length()) / 2, 0);
        // Calculate the number of spaces to pad on each side.
        // (width - text.length()) gives total extra space available.
        // Dividing by 2 gives the padding on one side.\
        // Math.max(..., 0) ensures the padding is not negative. The '0' makes the width at least 0, even if its negative
        return String.format("%" + padding + "s%s%" + padding + "s", "", text, "");
        // Construct the centered string using String.format.
        // "%" + padding + "s" creates a formatted string field that pads with spaces to a total width of 'padding'.
        // The first "%" + padding + "s" adds left padding, then %s is where the text goes,
        // and the second "%" + padding + "s" adds right padding.
        // For example, if padding is 3 and text is "Hello", the format becomes "%3s%s%3s"
        // and with the arguments ("", "Hello", "") it produces "   Hello   ". (this is the portions after the comma in the String.format in line 568)
        // and if arguments were ("a", "Hello", "a"), it produces "     aHello     a"

    }

    public static void main(String[] args) {
        AnsiConsole.systemInstall(); 
        // AnsiConsole.systemInstall() comes from the Jansi library:
        // (import org.fusesource.jansi.AnsiConsole)
        //It wraps System.out and System.err with special streams that interpret those ANSI escape codes
        // — so even on Windows CMD, the output looks properly styled.

        // It replaces the standard System.out and System.err with special streams
        // that support ANSI escape codes (e.g. \033[31m → red text) — enabling things like:
        // - Colored text
        // - Bold text
        // - Cursor movement
        // - Background colors

        // This ensures that ANSI formatting works properly even on terminals
        // (like Windows CMD) that don’t support ANSI codes by default.

        startGame(); //calls startGame method from GameMenu

        AnsiConsole.systemUninstall();
    }
}
