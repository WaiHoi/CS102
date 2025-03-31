package cardgame.network;

import java.util.*;
import java.util.concurrent.*;

import java.io.*;
import java.net.*;

import cardgame.game.*;
import cardgame.network.*;
import cardgame.model.*;
import cardgame.utility.UsernameValidator;

public class ClientHandler implements Runnable {

    /* ========= OUTPUT TAGS ========== */
    // add space after tag for message formatting
    public static final String TAG_SERVER = "[SERVER] ";
    public static final String TAG_PUBLIC = "[PUBLIC] ";
    public static final String TAG_PRIVATE = "[PRIVATE] ";
    public static final String TAG_INPUT = "[INPUT] ";
    public static final String TAG_ERROR = "[ERROR] ";
    public static final String TAG_CHAT = "[CHAT] ";

    /* ========= CORE ATTRIBUTES ========== */
    // loop through clients and send message to each client
    // broadcast message to multiple players
    public static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private final BlockingQueue<String> gameInputQueue = new LinkedBlockingQueue<>();
    private static boolean isNetworkMode = true;

    private final Socket socket;
    private final int playerID;

    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;

    /* ========= CONSTRUCTOR ========== */
    public ClientHandler(Socket socket, int playerID) throws IOException {

        // initialise the final field first
        this.socket = socket;
        this.playerID = playerID;

        // input and output streams
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    /* ========== MAIN FUNCTION ========== */
    @Override
    public void run() {

        try {

            // validate username
            this.clientUsername = checkUsername();

            // add to hashmap
            synchronized (clients) {
                clients.put(playerID, this);
            }

            gameOutput(TAG_SERVER + clientUsername + " has joined!");

            /* === Read Client's Messages === */
            // program will wait for client's message
            // separate thread for each client => rest of program can still run
            String messageFromClient;

            while ((messageFromClient = in.readLine()) != null) {
                handleMessage(messageFromClient);
            }

        } catch (IOException e) {
            gameOutput(TAG_ERROR + clientUsername + " disconnected");

        } finally {
            synchronized (clients) {
                closeEverything(socket, in, out);
            }
        }
    }

    private void handleMessage(String message) throws IOException {

        if (message.startsWith("[SERVER] SET_TURN:")) {
            int currentTurn = Integer.parseInt(message.substring(17));
            TurnManager.setCurrentPlayer(currentTurn);
            return;
        }

        /* === EDGE CASE 1: Empty/Null Messages === */
        if (message == null || message.trim().isEmpty()) {
            return;
        }

        /* === EDGE CASE 2: Long Messages === */
        if (message.length() > 1000) {
            send(TAG_ERROR + "Message too long");
            return;
        }

        /* === EDGE CASE 3: Malformed Tag === */
        if (message.startsWith("[") && !message.contains("]")) {
            send(TAG_ERROR + "Malformed tag");
            return;
        }

        /* === Handle Game Commands === */
        if (message.startsWith("/")) {
            handleGameCommand(message);

        } else if (message.startsWith("@")) {
            handleChatMessage(message);

        } else {
            handleTaggedMessage(TAG_INPUT + message);

        }
    }

    /* ========= NETWORK METHODS ========== */
    public void send(String message) throws IOException {

        if (!socket.isClosed()) {
            out.write(message);
            out.newLine();
            out.flush();

        } else {
            System.out.println(message);
        }
    }

    public static void broadcastTurnUpdate(int playerID) {
        if (isNetworkMode) {
            for (ClientHandler client : clients.values()) {
                try {
                    client.send("[SERVER] SET_TURN:" + playerID);
                } catch (IOException e) {
                    client.closeEverything(client.socket, client.in, client.out);
                }
            }
        }
    }

    /* ========= MESSAGE OUTPUT (NETWORK & CONSOLE) ========== */

    // println
    public static void gameOutput(String message) {
        // adds a new line
        processOutput(message, true);
    }

    // print
    public static void gameOutputRaw(String message) {
        processOutput(message, false);
    }

    // printf
    // uses variable length argument
    public static void gameOutputf(String format, Object... args) {
        // format message and adds new line
        processOutput(String.format(format, args), true);
    }

    // cleans message
    public static void processOutput(String message, boolean addNewLine) {

        // remove colour codes
        String cleanMessage = message.replaceAll("\u001B\\[[;\\d]*m", "");

        // network mode
        if (isNetworkMode) {
            // send to current client

            if (cleanMessage.startsWith(TAG_PRIVATE)) {
                // to player only
                int targetPlayerID = TurnManager.getCurrentPlayerID();

                if (targetPlayerID <= 0) {
                    System.err.println("[ERROR] Invalid current player ID: " + targetPlayerID);
                    return;
                }

                ClientHandler recipient = clients.get(TurnManager.getCurrentPlayerID());

                if (recipient != null) {
                    try {
                        // Strip tag before sending (client will re-add it)
                        recipient.send(cleanMessage.substring(TAG_PRIVATE.length()) +
                                (addNewLine ? "\n" : ""));
                    } catch (IOException e) {
                        System.out.println("Failed to send message");
                        recipient.closeEverything(recipient.socket, recipient.in, recipient.out);
                    }
                } else {
                    System.out.println("[ERROR] No recipient found");
                    System.out.println("Active clients: " + clients.keySet());
                }
            } else {
                for (ClientHandler client : clients.values()) {
                    try {
                        client.send(cleanMessage.substring(cleanMessage.indexOf("]") + 1));
                    } catch (IOException e) {
                        client.closeEverything(client.socket, client.in, client.out);
                    }
                }
            }

            // console mode
        } else {
            String consoleMessage = cleanMessage;
            if (cleanMessage.startsWith(TAG_PUBLIC) ||
                    cleanMessage.startsWith(TAG_PRIVATE) ||
                    cleanMessage.startsWith(TAG_CHAT)) {

                consoleMessage = cleanMessage.substring(cleanMessage.indexOf("]") + 1);
            }

            System.out.print(consoleMessage + (addNewLine ? "\n" : ""));

        }
    }

    /* ========== MESSAGE INPUT ========== */
    // public String getGameInput() throws InterruptedException {
    //     return gameInputQueue.take(); // Blocks until input arrives
    // }

    /* ========== COMMAND HANDLING ========== */
    private void handleGameCommand(String command) throws IOException {

        command = command.toLowerCase().trim();
        switch (command.toLowerCase()) {
            case "/quit":
                send(TAG_SERVER + "You have left the game. Goodbye!");
                closeEverything(socket, in, out);
                break;

            case "/help":
                sendHelpMenu();
                break;

            default:
                if (command.length() > 1000) {
                    send("[ERROR] Message too long");
                    return;
                }
                // broadcast to others
                gameOutput("[CHAT] " + clientUsername + ": " + command);
                // override self message
                send("[CHAT] You: " + command);
        }
    }

    private void handleChatMessage(String message) throws IOException {

        // basic validation
        // if (message.length() < 8 || message.charAt(0) != '[') {
        // send("[ERROR] Invalid message format");
        // return;
        // }

        gameOutput(message);
    }

    private void handleTaggedMessage(String message) throws IOException {

        // basic validation
        // if (message.length() < 8 || message.charAt(0) != '[') {
        // send("[ERROR] Invalid message format");
        // return;
        // }

        gameOutput(message);
    }

    private void sendHelpMenu() throws IOException {
        send("\n===================");
        send("Available Commands:");
        send("===================\n");
        send("/quit - Exit the game");
        send("/help - Show this help\n");
    }

    /* ========== UTILITY METHODS ========== */
    private String checkUsername() throws IOException {

        while (true) {
            String requestedUsername = in.readLine();

            if (requestedUsername == null) {
                throw new IOException("[SERVER] Client disconnected");
            }

            if (UsernameValidator.checkUsername(requestedUsername)) {
                return requestedUsername;
            } else {
                this.send(TAG_SERVER + "Username already taken.\nPlease try another: \n");
            }

        }
    }

    private boolean isPlayerTurn() {
        return TurnManager.getCurrentPlayerID() == this.playerID;
    }

    /* ========== CLEANUP ========== */
    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {

        // ensure other methods do not write if client is closing
        synchronized (clients) {
            // remove individual connection
            removeClient();
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (socket != null) {
                    socket.close();
                    gameOutput(String.format(TAG_SERVER + "%s has disconnected.\n", clientUsername));
                }
            } catch (IOException e) {
                System.out.println("[SERVER] Error closing resources for " + clientUsername);
                e.printStackTrace();
            }
        }
    }

    private void removeClient() {

        clients.remove(playerID);
        UsernameValidator.removeUsername(clientUsername);
        gameOutput("[SERVER] " + clientUsername + " has left the game");
    }

    /* ========== GETTERS & SETTERS ========== */
    public int getPlayerID() {
        return playerID;
    }

    public String getClientUsername() {
        return this.clientUsername;
    }

    public static boolean getNetworkMode() {
        return isNetworkMode && !clients.isEmpty();
    }

    public static void setNetworkMode(boolean mode) {
        isNetworkMode = mode;
    }

    public static ClientHandler getClientById(int playerId) {
        return clients.get(playerId);
    }

    public static boolean clientExists(int playerId) {
        return clients.containsKey(playerId);
    }

    public static Set<Integer> getAllPlayerIDs() {
        return clients.keySet();
    }

}
