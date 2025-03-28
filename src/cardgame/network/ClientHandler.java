package cardgame.network;

import java.util.*;
import java.util.concurrent.*;

import java.io.*;
import java.net.*;

import cardgame.game.*;
import cardgame.network.*;
import cardgame.utility.UsernameValidator;

public class ClientHandler implements Runnable {

    // loop through clients and send message to each client 
    // broadcast message to multiple players 
    public static final List<ClientHandler> clientHandlers = new CopyOnWriteArrayList<>();

    private final Socket socket;
    private final int playerID;

    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;

    public ClientHandler(Socket socket, int playerID) {

        // initialise the final field first 
        this.socket = socket;
        this.playerID = playerID;

            try {

                // input and output streams
                this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                this.clientUsername = checkUsername();

                // add clientHandler object to arraylist
                clientHandlers.add(this);

                // broadcast a message to other clients
                broadcastToAll(String.format("[SERVER] %s (ID:%d) has joined!", 
                                    clientUsername, playerID));

            } catch (IOException e) {
                // close everything if error occurs 
                closeEverything(socket, in, out);
            }
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getClientUsername() {
        return this.clientUsername;
    }

    private String checkUsername() throws IOException {

        while (true) {
            String requestedUsername = in.readLine();

            if (requestedUsername == null) {
                sendToPlayer("[SERVER] Client disconnected");
            }

            if (UsernameValidator.checkUsername(requestedUsername)) {
                return requestedUsername;
            } else {
                sendToPlayer("[SERVER] Username already taken.\nPlease choose another: \n");

            }

        }
    }

    /*** Method 2 ***/
    @Override
    public synchronized void run() {

        while (socket.isConnected()) {
            try {
                // program will wait for client's message 
                // separate thread for each client => rest of program can still run
                String messageFromClient = in.readLine();

                // if any issue occurs 
                if (messageFromClient == null) {
                    closeEverything(socket, in, out);
                    break;
                } 

                // handle commands from client
                switch (messageFromClient.toLowerCase()) {
                    case "/quit":
                        handleQuit();
                        break;
                    case "/help":
                        sendToPlayer("\nAvailable Commands:");
                        sendToPlayer("/quit - Exit the game");
                        sendToPlayer("/help - Show this help\n");
                        break;
                    default: 
                        // Add to game logic first
                        // if (game != null) {
                        //     game.processMove(clientUsername, messageFromClient);
                        // }
                        // Then broadcast to others
                        broadcastToOthers("[GAME] " + clientUsername + ": " + messageFromClient);                    }

            } catch (IOException e) {
                closeEverything(socket, in, out);
                break;
            }
        }
    }

    public synchronized void broadcastToOthers(String msgToSend) {

        // for each clientHandler in the list
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // send to other users with different username
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.out.write(msgToSend);
                    // send new line -> bufferedReader waits for a newLine() character
                    clientHandler.out.newLine();

                    // flush clientHandler
                    // message might not be big enough to fill entire buffer
                    // manually flush message
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
            }
        }
    }

    public synchronized void broadcastToAll(String msgToAll) {
        // for each clientHandler in the list
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.out.write(msgToAll);
                clientHandler.out.newLine();
                clientHandler.out.flush();

            } catch (IOException e) {
                closeEverything(socket, in, out);
            }
        }
    }

    public synchronized void sendToPlayer(String msgToPlayer) {
        // message to player only 
        try {
            if (!socket.isClosed()) {
                out.write(msgToPlayer);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    private void handleQuit() {
        sendToPlayer("[SERVER] You have left the game. Goodbye!");
        closeEverything(socket, in, out);
        return;

    }

    private void removeClient() {

        clientHandlers.remove(this);
        UsernameValidator.removeUsername(clientUsername);
        broadcastToAll("[SERVER] " + clientUsername + " has left the game");

    }

    private void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        // remove individual connection
        removeClient();
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
                System.out.printf("%s has disconnected.\n", clientUsername);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
