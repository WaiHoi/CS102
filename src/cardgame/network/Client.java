package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.*;

/*
 * constructor:
 *      -> setup input and output
 * 
 * method 1: sendMessage()
 *      -> sends any messages from clients 
 *      -> allows them to quit/exit the game 
 * 
 * method 2: listenForMessages()
 *      -> listens for any messages other clients have sent to the server
 * 
 * method 3: closeEverything()
 *      -> close all input and output streams
 *      -> close socket and connections
 * 
 * method 4: main()
 *      -> allows clients to enter their username
 *      -> runs both method 1 and 2 at the same time (separate threads)
 */

public class Client {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    /*** Constructor : setup input and output */
    public Client(Socket socket, String username) {
        try {
            // setting up input and output
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

            System.out.println("You are connected");

        } catch (IOException e) {
            closeEverything(socket, in, out);
            System.out.println("Error occurred: Client");
        }
    }
    /*** Method 1: Send Messages  */
    public void sendMessage() {
        try {
            Scanner sc = new Scanner(System.in);

            // send username to server
            out.write(username);
            out.newLine();
            out.flush();
            // Runs a loop to continously hear the user's input
            while (socket.isConnected()) {
                String messageToSend = sc.nextLine();
                // Allows the user to disconnect from the system
                if (messageToSend.equalsIgnoreCase("/quit")) {
                    System.out.println("Disconnecting from the server");
                    closeEverything(socket, in, out);
                    System.exit(0);
                }

                out.write(username + ": " + messageToSend);
                out.newLine();
                out.flush();
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
            System.out.println("Error occurred: sendMessage()");
        }
    }
    /*** Method 2: Listening for messages */
    public void listenForMessage() {
        // Create a new thread for incoming messages
        new Thread(new Runnable() {
            @Override
            public void run() {
                String msgFromChat;

                // waits for broadcast message
                while (socket.isConnected()) {
                    try {
                        msgFromChat = in.readLine();
                        // Checks if the connection to the server has to be closed
                        if (msgFromChat == null) {
                            System.out.println("Server has disconnected");
                            closeEverything(socket, in, out);
                            System.exit(0);
                        }


                        System.out.println(msgFromChat);
                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                        System.out.println("Error occurred: listenformessage()");
                        break;
                    }
                }
            }
        }).start();
    }
    /*** Method 3: Close Everything */
    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
        // Ensures all the connections and streams are closed
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

    public static void main(String args[]) throws IOException 
    {
        Scanner sc = new Scanner(System.in);
        System.out.printf("Enter your username: ");
        String username = sc.nextLine();
        
        try {
            // create a socket to connect to the server running on localhost at port number 1234
            Socket socket = new Socket("localhost", 1234);

            Client client = new Client(socket, username);

            // run while socket is running
            // separate thread -> both will run at the same time 
            client.listenForMessage();
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }
    }
}

