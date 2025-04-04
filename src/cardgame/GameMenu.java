package cardgame;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import cardgame.*;
import cardgame.game.*;
import cardgame.io.input.*;
import cardgame.io.output.*;
import cardgame.model.Bot;
import cardgame.model.Human;
import cardgame.network.*;
import cardgame.utility.*;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

public class GameMenu {

    public static String username;
    public static List<String> usernames = new ArrayList<>();
    public static int numHumans;
    public static int numBots;

    // create new thread
    private static Thread serverThread;
    private static final AtomicBoolean serverReady = new AtomicBoolean(false);    

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
        System.out.println(ansi().fgBrightCyan().a("1. Host & Play (Start Server + Client)"));
        System.out.println(ansi().fgBrightMagenta().a("2. Join Existing Game (Client Only)"));
        System.out.println(ansi().fgBrightYellow().a("3. Play Locally (Console Mode)"));
        System.out.println(ansi().fgBrightRed().a("4. Exit\n"));
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

    public static void readOption() {
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

    private static List<String> getPlayerNames(int numPlayers) {
        Scanner sc = new Scanner(System.in);

        List<String> usernames = new ArrayList<>();


        for (int i = 0; i < numPlayers; i++) {

            while (true) {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                String name = sc.nextLine().strip();
    
                if (UsernameValidator.checkUsername(name)) {
                    usernames.add("Player " + name);
                    break;
                } else {
                    System.out.println("Username '" + name + "' is taken. Try another.");
                }
            }
        }
        return usernames;

    }

    public static void hostAndPlay() {
        
        displayPlayerSetup();

        // initialise network mode
        ClientHandler.setNetworkMode(true);

        // start server with thread management 
        serverThread = new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(1234);
                ParadeServer server = new ParadeServer(serverSocket, true, numHumans, numBots);
                serverReady.set(true);
                server.startServer();

            } catch (IOException e) {
                System.out.println("[SERVER] Failed to start the server.\nStarting local game...");
                fallbackToLocalGame();
            }
        });
        serverThread.start();

        // wait for server to be ready
        if (!waitForServerReady(5000)) {
            System.out.println("[SERVER] Server startup timed out.\nStarting local game...");
            fallbackToLocalGame();
            return;
        }

        // 1 second buffer after port has opened
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // wait for connections
        System.out.println("[SERVER] Waiting for connections...\n");
        connectToGame("localhost");

    }

    // checks if server's port is open 
    // wait for server before client connects 
    private static boolean waitForServerReady(int timeout) {
        long start = System.currentTimeMillis();
        while (!serverReady.get() && (System.currentTimeMillis() - start) < timeout) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return serverReady.get();
    }

    private static void joinGame() {
        connectToGame("localhost");

    }

    private static void fallbackToLocalGame() {
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
        ClientHandler.setNetworkMode(false);
        startLocalGame();
    }

    public static void startLocalGame() {

        ClientHandler.setNetworkMode(false);
        
        displayPlayerSetup();
        Player.players.clear();

        // unique usernames 
        usernames = getPlayerNames(numHumans);

        /* === Initialise Players and Bots === */
        // console mode
        for (int i = 0; i < numHumans; i++) {
            int playerID = i + 1;
            ConsoleInput input = new ConsoleInput();
            ConsoleOutput output = new ConsoleOutput();
            Player.players.add(new Human(usernames.get(i), playerID, output, input));
        }

        for (int i = 0; i < numBots; i++) {
            Player.players.add(new Bot("Bot " + i, new ConsoleOutput()));
        }

        Initialize.initializeVariables();
        Game.mainFunction();
        Score.calculateScore();

    }

    private static void connectToGame(String host) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your username: ");
        username = sc.nextLine().trim();

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

        // GameMenu menu = new GameMenu(); //static methods so dunnid to create object instance
        readOption();

        // Initialize.initializeVariables();
        // Game.mainFunction();

        AnsiConsole.systemUninstall();
        // Score score = new Score();
        // score.isWinner(g.players, g);
    }

}