package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
            System.out.println("Waiting for " + numHumans + " players...");

            // keep server running until serverSocket is closed
            while (!serverSocket.isClosed() && ClientHandler.clients.size() < numHumans) {

                // wait and accept client connection
                Socket socket = serverSocket.accept();
                int playerID = ClientHandler.clients.size() + 1;

                // creates a new clientHandler
                // initialiases I/O
                ClientHandler clientHandler = new ClientHandler(socket, playerID);

                // create and execute new thread for each player
                Thread thread = new Thread(clientHandler);
                thread.start();

            }

            /* ===== Initialise ===== */
            // start game once all connected
            if (ClientHandler.clients.size() == GameMenu.numHumans) {
                initialiseGame();

            } else {
                System.out.println("Server stopped before all players connected");
            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }

    public void initialiseGame() throws IOException {

        // clear existing players
        Player.players.clear();

        for (Map.Entry<Integer, ClientHandler> entry : ClientHandler.clients.entrySet()) {
            Integer id = entry.getKey();
            ClientHandler client = entry.getValue();

            NetworkOutput networkOutput = new NetworkOutput(client);
            NetworkInput networkInput = new NetworkInput(client, networkOutput);

            Player.players.add(new Human(
                    client.getClientUsername(),
                    client.getPlayerID(),
                    networkOutput,
                    networkInput));
        }

        for (int i = 1; i <= GameMenu.numBots; i++) {
            Player.players.add(new Bot(
                    "Bot " + i,
                    new ConsoleOutput()));
        }

        // initialise turnmanager
        TurnManager.initialize(isNetworkMode, numHumans, numBots, null);
        TurnManager.setCurrentPlayer(1);
        Initialize.initializeVariables();

        // broadcast initial state
        // String gameState = "Game started with:\n" +
        //         "- Humans: " + numHumans + "\n" +
        //         "- Bots: " + numBots + "\n" +
        //         "- First Player: " + TurnManager.getCurrentPlayerID();

        // for (Map.Entry<Integer, ClientHandler> entry : ClientHandler.clients.entrySet()) {
        //     ClientHandler client = entry.getValue();
        //     ((NetworkOutput) client.getOutput()).broadcastGameState(gameState);
        // }
    
        // Start game in a separate thread to avoid blocking
        new Thread(() -> {
            Game.mainFunction(true);
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
