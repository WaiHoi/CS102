package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.*;

import cardgame.*;
import cardgame.game.*;
import cardgame.io.input.*;
import cardgame.io.output.*;
import cardgame.model.*;
import cardgame.utility.*;


public class ParadeServer {
    // object to listen for connections
    private ServerSocket serverSocket;
    // thread-safe counter
    private final boolean isNetworkMode;
    private final int numHumans;
    private final int numBots;
    
    // constructor
    public ParadeServer(ServerSocket serverSocket, boolean isNetworkMode,
                            int numHumans, int numBots) {
        this.serverSocket = serverSocket;
        this.isNetworkMode = isNetworkMode;
        this.numHumans = numHumans;
        this.numBots = numBots;
        ClientHandler.setNetworkMode(isNetworkMode);
    }

    public void startServer() {

        // error handling
        try {

            /* ===== Accept Player Connections ===== */
            List<ClientHandler> connectedPlayers = new ArrayList<>();
            System.out.println("Waiting for " + numHumans + " players...");

            // keep server running until serverSocket is closed 
            while (!serverSocket.isClosed() && connectedPlayers.size() < numHumans) {

                // wait and accept client connection
                Socket socket = serverSocket.accept();
                int playerID = connectedPlayers.size() + 1;

                // creates a new clientHandler
                // initialiases I/O
                ClientHandler clientHandler = new ClientHandler(socket, playerID);
                connectedPlayers.add(clientHandler);

                // create and execute new thread for each player
                Thread thread = new Thread(clientHandler);
                thread.start(); 

                // wait for client to send username
                // while (clientHandler.getClientUsername() == null) {
                //     try {
                //         Thread.sleep(500);
                //     } catch (InterruptedException e) {
                //         Thread.currentThread().interrupt();
                //     }
                // }
                
            } 

            /* ===== Initialise ===== */
            // start game once all connected
            if (connectedPlayers.size() == GameMenu.numHumans) {
                initialiseGame(connectedPlayers);

            } else {
                System.out.println("Server stopped before all players connected");
            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void initialiseGame(List<ClientHandler> clientHandlers) throws IOException {

        // clear existing players
        Player.players.clear();
        NetworkOutput output = new NetworkOutput(null);

        for (ClientHandler client : clientHandlers) {
            
            NetworkOutput networkOutput = new NetworkOutput(client);
            NetworkInput networkInput = new NetworkInput(client, networkOutput);


            Player.players.add(new Human(
                client.getClientUsername(),
                client.getPlayerID(),
                networkOutput,
                networkInput
            ));
        }

        for (int i = 1; i <= GameMenu.numBots; i++) {
            Player.players.add(new Bot(
                "Bot " + i, 
                new NetworkOutput(null)));
        }

        // initialise turnmanager 
        TurnManager.initialize(isNetworkMode, numHumans, numBots, output);
        Initialize.initializeVariables();

        // broadcast initial state
        String gameState = "Game started with:\n" +
                         "- Humans: " + numHumans + "\n" +
                         "- Bots: " + numBots + "\n" +
                         "- First Player: " + TurnManager.getCurrentPlayerID();
        output.broadcastGameState(gameState);

        // Start game in a separate thread to avoid blocking
        new Thread(() -> {
            Game.mainFunction();
        }).start();

    }

    public void closeServerSocket() {
                
        // ensure serverSocket is not null
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

