package cardgame.network;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

import cardgame.game.*;
import cardgame.io.output.*;
import cardgame.io.input.*;
import cardgame.network.*;
import cardgame.model.*;
import cardgame.utility.UsernameValidator;

public class ClientHandler implements Runnable {

    /* ========= CORE ATTRIBUTES ========== */
    // loop through clients and send message to each client
    // broadcast message to multiple players
    public static final Map<Integer, ClientHandler> clients = new ConcurrentHashMap<>();
    private static final ThreadLocal<ClientHandler> currentClient = new ThreadLocal<>();
    private static boolean isNetworkMode = true;

    private final Socket socket;
    private final int playerID;
    private final GameOutput output;
    private final GameInput input;
    private final BufferedWriter out;
    private String clientUsername;

    /* ========= CONSTRUCTOR ========== */

    // network constructor
    public ClientHandler(Socket socket, int playerID) throws IOException {
        this.socket = socket;
        this.playerID = playerID;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // add to hashmap
        synchronized (clients) {
            clients.put(playerID, this);
        }
        
        // input and output streams
        this.output = new NetworkOutput(this);
        // this.input = new NetworkInput(socket.getInputStream(), output);
        this.input = new NetworkInput(this, output);

    }

    /* ========== MAIN FUNCTION ========== */
    @Override
    public void run() {
        currentClient.set(this);

        try {

            // send to client to tell that server is ready
            out.write("SERVER_READY\n");
            out.newLine();
            out.flush();

            // validate username
            this.clientUsername = checkUsername();

            // // add to hashmap
            // synchronized (clients) {
            //     clients.put(playerID, this);
            // }
            output.broadcastToAll(clientUsername + " has joined!");

            /* === Read Client's Messages === */
            // program will wait for client's message
            // separate thread for each client => rest of program can still run
            String messageFromClient;

            while ((messageFromClient = input.readLine("")) != null) {

                if (TurnManager.isMyTurn(playerID)) {

                    System.out.println("[DEBUG] Message received");
                    handleMessage(messageFromClient);

                } else {
                    output.sendError("Wait for your turn!");
                }
            }

        } catch (IOException e) {
            output.broadcastToAll(clientUsername + " disconnected");

        } finally {
            synchronized (clients) {
                closeResources();
            }
            currentClient.remove();
        }
    }

    /* ========= MESSAGE HANDLING ========== */
    private void handleMessage(String rawMessage) throws IOException {

        /* === EDGE CASE 1: Empty/Null Messages === */
        if (rawMessage == null || rawMessage.trim().isEmpty()) {
            return;
        }

        String trimmed = rawMessage.trim();

        // 1. Handle commands (/quit, /help)
        if (trimmed.startsWith("/")) {
            handleGameCommand(trimmed);
        } 
        // 2. Handle chat (@message)
        else if (trimmed.startsWith("@")) {
            handleChatMessage(trimmed.substring(1));
        }
        // 3. Ignore numbers (processed by NetworkInput)
        else if (trimmed.matches("\\d+")) {
            return; // No-op
        }
        // 4. Reject other garbage
        else {
            output.sendError("Type @ to chat or /help for commands.");
        }
    }

    /* ========== COMMAND HANDLING ========== */
    private void handleGameCommand(String command) throws IOException {

        command = command.toLowerCase().trim();

        switch (command.toLowerCase()) {
            case "/quit":
                output.sendPrivate("You have left the game. Goodbye!");
                closeResources();
                break;

            case "/help":
                showHelpMenu();
                break;

            default:
                output.sendError("Unknown command. Type /help for options.");
        }
    }

    private void showHelpMenu() throws IOException {
        output.sendPrivate("\n===================");
        output.sendPrivate("Available Commands:");
        output.sendPrivate("===================\n");
        output.sendPrivate("/quit      - Exit the game");
        output.sendPrivate("/help      - Show this help");
        output.sendPrivate("@message   - Send chat to players");
        output.sendPrivate("1-5        - Play a card from your hand");
    }

    /* ========= CHAT HANDLING ========== */
    private void handleChatMessage(String message) {
        if (message.isEmpty()) {
            output.sendError("Usage: @your_message_here");
            return;
        }
        output.broadcastToAll(clientUsername + ": " + message);
    }

    /* ========= GAME ACTIONS ========== */
    // private void handleGameAction(String message) {
    //     try {
    //         // Case 1: Numeric input (card selection)
    //         if (message.equals("\\d+")) {

    //             int number = Integer.parseInt(message);

    //             if (number >= 1 && number <= 5) {
    //                 output.sendServer("" + number);

    //             } else {
    //                 output.sendError("Card positions must be 1-5");

    //             }
    //         }
    //     } catch (Exception e) {
    //         output.sendError("Processing error: " + e.getMessage());
    //     }
    // }

    /* ========= NETWORK METHODS ========== */
    public synchronized void send(String message) throws IOException {
        out.write(message);
        out.newLine();
        out.flush();
    }

    /* ========== UTILITY METHODS ========== */
    private String checkUsername() throws IOException {

        while (true) {
            String requestedUsername = input.readLine("");

            if (requestedUsername == null) {
                throw new IOException("Client disconnected");
            }

            if (requestedUsername.isEmpty()) {
                output.sendError("Username cannot be empty. Please try again:");
                continue;
            }

            if (UsernameValidator.checkUsername(requestedUsername)) {
                return requestedUsername;

            } else {
                output.sendError("Username already taken.\nPlease try another: \n");
            }

        }
    }

    /* ========== CLEANUP ========== */
    private void closeResources() {

        // ensure other methods do not write if client is closing
        synchronized (clients) {
            // remove individual connection
            removeClient();
            try {
                if (socket != null) {
                    socket.close();
                    output.broadcastToAll(clientUsername + " has disconnected.\n");

                }
            } catch (IOException e) {
                output.sendError("Error closing resources for " + clientUsername);
                e.printStackTrace();
            }
        }
    }

    private void removeClient() {

        clients.remove(playerID);
        UsernameValidator.removeUsername(clientUsername);
        output.broadcastToAll(clientUsername + " has left");
    }

    /* ========= CLIENT COMMUNICATION ========== */

    public static ClientHandler getCurrentClient() {
        return currentClient.get();
    }

    public static GameOutput getCurrentClientOutput() {
        ClientHandler handler = currentClient.get();
        if (handler != null) {
            return handler.output;
        }
        return new ConsoleOutput();
    }

    public static GameInput getCurrentClientInput() {
        ClientHandler handler = currentClient.get();
        if (handler != null) {
            return handler.input;
        }
        return new ConsoleInput();
    }

    // get thread of current player
    public static ClientHandler getHandlerForPlayer(int playerID) {
        return clients.get(playerID);
    }

    /* ========== GETTERS & SETTERS ========== */
    public int getPlayerID() {
        return playerID;
    }

    public String getClientUsername() {
        return this.clientUsername;
    }

    public Socket getSocket() throws IOException {
        if (this.socket == null || this.socket.isClosed()) {
            throw new IOException("No active socket connection");
        }
        return this.socket;
    }

    // current player's output
    public GameOutput getOutput() {
        return this.output;
    }

    // current player's input
    public GameInput getInput() {
        return this.input;
    }

    public static boolean isNetworkMode() {
        return isNetworkMode && !clients.isEmpty();
    }

    public static void setNetworkMode(boolean mode) {
        isNetworkMode = mode;
    }

    public static boolean clientExists(int playerId) {
        return clients.containsKey(playerId);
    }

}
