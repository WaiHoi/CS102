package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.*;

import cardgame.*;
import cardgame.game.*;
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
                int playerID = nextPlayerID.getAndIncrement();

                // creates a new clientHandler
                ClientHandler clientHandler = new ClientHandler(socket, playerID);

                // create and execute new thread for each player
                Thread thread = new Thread(clientHandler);
                thread.start();

                if (nextPlayerID.get() > GameMenu.numHumans) {
                    System.out.println("[SERVER] All players connected! Game Starting!");
                    Initialize.initializeVariables();
                    Game.mainFunction();
                }
            }

        } catch (IOException e) {
            closeServerSocket();
        }
    }

    /*** Method 2 ***/
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

