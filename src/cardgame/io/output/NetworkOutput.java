package cardgame.io.output;

import java.io.IOException;

import cardgame.network.*;

public class NetworkOutput implements GameOutput {
    private final ClientHandler currentClient;
    private final int currentPlayerId;
    private String lastSentGameState;

    public NetworkOutput(ClientHandler client) {
        this.currentClient = client;

        if (currentClient != null) {
            this.currentPlayerId = currentClient.getPlayerID();
        } else {
            this.currentPlayerId = -1;
        }
    }

    /* ===== CORE METHODS ===== */

    @Override
    // adds new line
    public void println(String message) {
        sendMessage(message + "\n");
    }

    @Override
    // no new line
    public void print(String message) {
        sendMessage(message);
    }

    @Override
    // formatted output
    public void printf(String format, Object... args) {
        // use println method
        println(String.format(format, args));
    }

    /* ===== SPECIFIC METHOD ===== */

    @Override
    public void sendServer(String message) {
        sendMessage(message);
    }

    @Override
    public void sendError(String message) {
        sendMessage(message);
    }

    @Override
    public void sendChat(String message) {
        sendMessage(message);
    }

    @Override
    public void sendPrivate(String message) {
        if (currentClient != null) {
            try {
                currentClient.send(message);
            } catch (IOException e) {
                System.out.println("Send failed: " + e.getMessage());
            }
        }
    }

    /* ===== BROADCAST MESSAGES ===== */
    @Override
    // all players  
    public void broadcastToAll(String message) {

        synchronized (ClientHandler.clients) { // Add synchronization
            for (ClientHandler client : ClientHandler.clients.values()) {
                try {
                    client.send(message);
                } catch (IOException e) {
                    System.err.println("Failed to send to " + client.getClientUsername());
                    ClientHandler.clients.remove(client.getPlayerID()); // Safe removal
                }
            }
        }
    }

    /* ===== GAME STATE METHODS ===== */
    public void broadcastGameState(String state) {

        if (!state.equals(lastSentGameState)) {
            String formattedState = "\n----- GAME STATE UPDATE -----\n" + 
                                    state + 
                                    "\n-----------------------------\n";
            broadcastToAll(formattedState);
            lastSentGameState = state;
        }

    }

    public void broadcastTurnUpdate(int currentPlayer) {
        String turnMessage = "\n----- TURN UPDATE -----\n" +
                           "Current Player: " + currentPlayer + "\n" +
                           (currentPlayerId == currentPlayer ? "Your Turn!\n" : "") +
                           "-----------------------\n";
        broadcastToAll(turnMessage);
    }

    /* ===== HELPER METHODs ===== */
    public void sendMessage(String message) {
        try {
            if (currentClient != null) {
                currentClient.send(message);
            }
        } catch (IOException e) {
            System.out.println("Send failed: " + e.getMessage());
        }
    }

}
