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

        if (socket == null) {
            throw new IllegalArgumentException("Socket cannot be null");
        }

        this.socket = socket;
        this.playerID = playerID;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        
        // input and output streams
        this.output = new NetworkOutput(this);
        this.input = new NetworkInput(this, output);

        // add to hashmap
        synchronized (clients) {
            clients.put(playerID, this);
        }

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

            // username validated in gamemenu
            String username = input.readLine("");
            System.out.println("[DEBUG] Received username: '" + username + "'");
            if (username != null && !username.trim().isEmpty()) {
                this.clientUsername = username.trim();
                output.broadcastToAll(clientUsername + " has joined!");
                // Store username in players list
                if (Player.players.size() >= playerID) {
                    Player.players.get(playerID-1).setName(clientUsername);
                }
            } else {
                throw new IOException("Invalid username received");
            }
            // notify if it's this player's turn
            if (TurnManager.isMyTurn(playerID)) {
                ((NetworkOutput) output).broadcastTurnUpdate(playerID);
            }

            /* === Read Client's Messages === */
            processMessage();

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
    private void processMessage() throws IOException {

        String messageFromClient;

        while ((messageFromClient = input.readLine("")) != null) {

            if (!TurnManager.isMyTurn(playerID)) {
                output.sendError("Wait for your turn!");
                continue;
            } 

            handleMessage(messageFromClient.trim());
        }
    }


    private void handleMessage(String message) throws IOException {

        /* === EDGE CASE 1: Empty/Null Messages === */
        if (message == null || message.isEmpty()) {
            return;
        }

        // 1. Handle commands (/quit, /help)
        if (message.startsWith("/")) {
            handleGameCommand(message);
        } 
        // 2. Handle chat (@message)
        else if (message.startsWith("@")) {
            handleChatMessage(message.substring(1));
        }
        // 3. Ignore numbers (processed by NetworkInput)
        else if (message.matches("\\d+")) {
            return; // No-op
        }
        // 4. Reject other garbage
        else {
            output.sendError("Invalid command. Type /help");
        }
    }

    /* ========== COMMAND HANDLING ========== */
    private void handleGameCommand(String command) throws IOException {

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
        if (!message.isEmpty()) {
            output.broadcastToAll(clientUsername + ": " + message);
        }
    }

    /* ========= NETWORK METHODS ========== */
    public synchronized void send(String message) throws IOException {
        out.write(message);
        out.newLine();
        out.flush();
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
        synchronized (clients) {
            return clients.get(playerID);
        }
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
