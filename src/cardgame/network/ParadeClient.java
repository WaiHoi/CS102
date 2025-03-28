package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.*;

import cardgame.network.*;

public class ParadeClient {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    public ParadeClient(Socket socket, String username) {
        
        this.username = username;

        try {
            this.socket = socket;
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

            // send username to server
            out.write(username);
            out.newLine();
            out.flush();

        } catch (IOException e) {
            closeEverything(socket, in, out);
            System.out.println("Error occurred: Client");
        }

    }

    public String getClientUsername() {
        return username;
    }

    public void startClient() {
        // run while socket is running
        // separate thread -> both will run at the same time 
        new Thread(this::listenForMessage).start(); 
        sendMessage();
    }

    public void sendMessage() {
        try {
            Scanner sc = new Scanner(System.in);

            while (true) {
                // remove whitespaces
                String message = sc.nextLine().strip();

                // only command that needs to be included 
                if (message.equalsIgnoreCase("/quit")) {
                    handleQuit();
                    return;
                } else if (message.equalsIgnoreCase("/help")) {
                    displayHelp();
                } else {
                    sendToServer(message);
                }
            }
        } catch (IOException e) {
            closeEverything(socket, in, out);
        }
    }

    // method to print to server from client 
    private void sendToServer(String message) throws IOException {
        if (out != null) {
            out.write(message);
            out.newLine();
            out.flush();
        }
    }

    private void handleQuit() throws IOException{

        sendToServer("/quit");
        System.out.println("Disconnected from server");
        
    }

    private void displayHelp() {
        System.out.println("\nAvailable Commands:");
        System.out.println("/help - Show this help");
        System.out.println("/quit - Exit the game\n");
    }

    // listen for any messages 
    public void listenForMessage() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;

                // waits for broadcast message
                while (socket.isConnected()) {
                    try {
                        message = in.readLine();

                        if (message == null) {
                            closeEverything(socket, in, out);
                            System.exit(0);
                        }

                        System.out.println(message);

                    } catch (IOException e) {
                        closeEverything(socket, in, out);
                        break;
                    }
                }
            }
        }).start();
    }

    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {
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

    public static void main(String[] args) throws IOException {
    
        Scanner sc = new Scanner(System.in);
        System.out.printf("Enter your username: ");
        String username = sc.nextLine();

        try {
            // create a socket to connect to the server running on localhost at port number 1234
            Socket socket = new Socket("localhost", 1234);

            ParadeClient client = new ParadeClient(socket, username);
            client.startClient();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }
        
    }
}

