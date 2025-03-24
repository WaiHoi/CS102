import java.util.*;
import java.io.*;
import java.net.*;

/* 
 * constructor: 
 *      -> setup input and output 
 *      -> add players to player class
 *      -> start game when all conditions are met 
 * 
 * method 1: run()
 *      -> run separate threads for each client 
 *      -> handles any game-related messages 
 *      -> should use a trigger .startsWith("[GAME]")
 * 
 * method 2: broadcastMessage()
 *      -> send messages to all clients 
 * 
 * method 3: removeClientHandler()
 *      -> remove from game 
 * 
 * method 4: closeEverything()
 *      -> close input and output
 *      -> close socket/connection
 */

public class ClientHandler implements Runnable {

    // loop through clients and send message to each client 
    // broadcast message to multiple players 
    public static List<ClientHandler> clientHandlers = new ArrayList<>();

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String clientUsername;
    /*** Constructor ***/
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;

            // input and output streams
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUsername = in.readLine();

            // add clientHandler object to arraylist
            clientHandlers.add(this);

            // broadcast a message to other clients
            broadcastMessage("[SERVER] " + clientUsername + " has joined the game!");

        } catch (IOException e) {
            // close everything if error occurs 
            closeEverything(socket, in, out);
            System.out.println("Error occurred: clienthandler");
        }
    }

    /*** Method 1 ***/
    @Override
    public void run() {

        while (socket.isConnected()) {
            try {
                // program will wait for client's message 
                // separate thread for each client => rest of program can still run
                String messageFromClient = in.readLine();

                if (messageFromClient == null) {
                    closeEverything(socket, in, out);
                    break;
                }

                broadcastMessage(messageFromClient);

            } catch (IOException e) {
                closeEverything(socket, in, out);
                System.out.println("Error occurred: run()");
                break;
            }
        }
    }

    /*** Method 2: Broadcasting messages */
    public void broadcastMessage(String messageToSend) {
        // for each clientHandler in the list
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // send to other users with different username
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.out.write(messageToSend);
                    // send new line -> bufferedReader waits for a newLine() character
                    clientHandler.out.newLine();

                    // flush clientHandler
                    // message might not be big enough to fill entire buffer
                    // manually flush message
                    clientHandler.out.flush();
                }
            } catch (IOException e) {
                closeEverything(socket, in, out);
                System.out.println("Error occurred: broadcastmessage()");
            }
        }
    }

    /*** Method 3: Remove Client handlers */
    public void removeClientHandler() {
        // remove user -> not send message to the connection
        clientHandlers.remove(this);
        broadcastMessage("[SERVER] " + clientUsername + " has left the game");

    }

    /*** Method 4: Close Everything ***/
    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        // remove individual connection
        removeClientHandler();
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
