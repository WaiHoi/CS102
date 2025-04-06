package cardgame.network;

import java.io.*;
import java.net.*;
import java.util.*;

public class ParadeClient {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private String username;

    /* ========= CONSTRUCTOR ========== */
    public ParadeClient(Socket socket, String username) throws IOException {
        this.socket = socket;
        this.username = username;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    public void startClient() {
        try {
            // wait for server to be ready before sending username
            if (!waitForServerReady()) {
                throw new IOException("Server handshake failed");
            }

            // send username to server
            sendToServer(username);

            // start a listener thread
            new Thread(this::listenForMessage).start();

            // start message sender
            sendMessage();

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
            closeEverything(socket, in, out);
        }
    }

    /* ========= HANDSHAKE PROTOCOL ========== */
    private boolean waitForServerReady() throws IOException {

        try {
            // timeout after 5 seconds
            socket.setSoTimeout(5000);
            String response = in.readLine();
            System.out.println("[DEBUG] Server response: " + response);
            // returns true 
            return "SERVER_READY".equals(response);

        } catch (SocketTimeoutException e) {
            System.out.println("Server connection timeout");
            return false;

        } finally {
            // resets timeout
            socket.setSoTimeout(0);
        }
    }

    /* ========= MESSAGE HANDLING ========== */
    // listen for any messages
    public void listenForMessage() {

        try {
            // continuously listens for messages 
            String message;
            while (!Thread.currentThread().isInterrupted() &&
                    (message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            System.err.println("Server connection lost");
        } finally {
            closeEverything(socket, in, out);
        }

    }

    public void sendMessage() throws IOException {
        Scanner sc = new Scanner(System.in);

        while (socket.isConnected()) {
            // remove whitespaces
            String message = sc.nextLine().trim();

            // basic validation
            if (message.isEmpty()) {
                continue;
            }

            if (message.length() > 1000) {
                System.out.println("Message too long (max 1000 chars)");
                continue;
            }

            if (message.equalsIgnoreCase("/quit") ||
                message.equalsIgnoreCase("/help")) {
                
                handleCommand(message);
            } else {
                sendToServer(message);
            }
            

        }
    }

    /* ========= COMMAND HANDLING ========== */
    private void handleCommand(String message) throws IOException {
        switch (message.toLowerCase()) {
            case "/quit":
                handleQuit();
                break;
            case "/help":
                displayHelp();
                break;
        }
    }

    private void handleQuit() throws IOException {

        sendToServer("/quit");
        System.out.println("Disconnected from server");
        closeEverything(socket, in, out);

    }

    private void displayHelp() {
        System.out.println("\n===================");
        System.out.println("Available Commands:");
        System.out.println("===================\n");
        System.out.println("/quit - Exit the game");
        System.out.println("/help - Show this help\n");
    }

    /* ========= SEND TO SERVER ========== */
    // method to print to server from client
    private void sendToServer(String message) throws IOException {
        if (out != null) {
            System.out.println("[DEBUG] Sending to server: " + message);
            out.write(message);
            out.newLine();
            out.flush();
        }
    }

    /* ========== CLEANUP ========== */
    public void closeEverything(Socket socket, BufferedReader in, BufferedWriter out) {

        try {
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("[SERVER] Error closing resources");
            e.printStackTrace();
        }
    }

    /* ===== FOR TESTING ===== */

    public static void main(String[] args) throws IOException {

        Scanner sc = new Scanner(System.in);
        System.out.printf("Enter your username: ");
        String username = sc.nextLine();

        try {
            // create a socket to connect to the server running on localhost at port number
            // 1234
            Socket socket = new Socket("localhost", 1234);
            ParadeClient client = new ParadeClient(socket, username);
            client.startClient();
        } catch (IOException e) {
            System.out.println("Failed to connect to server");
        }

    }
}
