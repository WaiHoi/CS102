package cardgame.network;

import java.io.*;
import java.net.*;

/*
 * method 1: startServer()
 *      -> ensure server keeps running
 *      -> starts the server and listens for connections to clients
 *      -> creates new thread for each client
 * 
 * method 2: closeServerSocket()
 *      -> closes the server socket 
 *      -> called in catch blocks if any error occurs 
 * 
 * method 3: main()
 *      -> start the new game 
 *      -> state sever port number
 *      -> start server
 */

public class Server {
    // object to listen for connections
    private ServerSocket serverSocket;
    
    // constructor
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /*** Method 1 ***/
    public void startServer() {

        // error handling
        try {

            System.out.println("Server is running and waiting for client connection...");

            // keep server running until serverSocket is closed 
            while (!serverSocket.isClosed()) {

                // wait and accept client connection
                Socket socket = serverSocket.accept();
                System.out.println("A new player has joined!");

                // responsible for communicating with client 
                // implement interface runnable 
                // instances will be executed by a separate thread
                // -> application will be able to handle multiple players 
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                // execute thread
                thread.start();

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

        // create a server socket on port number 1234
        // listen for connections on this port number 
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();

    }
}

