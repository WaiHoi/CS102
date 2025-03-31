package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.*;

import cardgame.*;
import cardgame.game.*;
import cardgame.model.*;
import cardgame.utility.Initialize;


public class ParadeServer {
    // object to listen for connections
    private ServerSocket serverSocket;
    // thread-safe counter
    private static final AtomicInteger nextPlayerID = new AtomicInteger(1);
    
    // constructor
    public ParadeServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void startServer() {

        // error handling
        try {

            // keep server running until serverSocket is closed 
            while (!serverSocket.isClosed() && nextPlayerID.get() <= GameMenu.numHumans) {

                // wait and accept client connection
                Socket socket = serverSocket.accept();

                if (nextPlayerID.get() > GameMenu.numHumans) {
                    System.out.println("Max players reached, rejecting connection");
                    socket.close();
                    continue;
                }

                int playerID = nextPlayerID.getAndIncrement();

                // creates a new clientHandler
                ClientHandler clientHandler = new ClientHandler(socket, playerID);

                // create and execute new thread for each player
                Thread thread = new Thread(clientHandler);
                thread.start();

                // start game once all connected
                if (nextPlayerID.get() > GameMenu.numHumans) {

                    // initialize network players 
                    for (int i = 1; i <= GameMenu.numHumans; i++) {
                        Player.players.add(new Human("Player " + i, i));
                    }
    
                    for (int i = 1; i <= GameMenu.numBots; i++) {
                        Player.players.add(new Bot("Bot " + i));
                    }

                    Initialize.initializeVariables();
                    TurnManager.initialize(true);
                    TurnManager.setCurrentPlayer(1);

                    System.out.println("Current player after init: " + TurnManager.getCurrentPlayerID());
                    System.out.println("Connected clients: " + ClientHandler.clients.keySet());
                    
                    // Start game in a separate thread to avoid blocking
                    new Thread(() -> {
                        Game.mainFunction();
                    }).start();
                } 
            }

        } catch (IOException e) {
            closeServerSocket();
        }
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

    public static void main(String args[]) throws IOException {
        
        ServerSocket serverSocket = new ServerSocket(1234);
        ParadeServer server = new ParadeServer(serverSocket);
        server.startServer();

    }
}

