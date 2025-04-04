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
        this.username = username;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    /* ========= HANDSHAKE PROTOCOL ========== */
    private boolean waitForServerReady() throws IOException {
        try {
            String serverResponse = in.readLine();

            if ("SERVER_READY".equals(serverResponse)) {
                return true;
            } else {
                System.out.println("[ERROR] Invalid server handshake");
                return false;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("[ERROR] Server handshake timeout");
            return false;
        }
    }


    public void startClient() {
        try {
            // wait for server to be ready before sending username
            if (!waitForServerReady()) {
                throw new IOException("Server handshake failed");
            }

            out.write(username);
            out.newLine();
            out.flush();

            // start a listener thread
            new Thread(this::listenForMessage).start();

            // start message sender
            sendMessage();

        } catch (Exception e) {
            System.out.println("Connection error: " + e.getMessage());
            closeEverything(socket, in, out);
        }
    }

    /* ========= MESSAGE HANDLING ========== */
    // listen for any messages
    public void listenForMessage() {

        String message;

        // waits for broadcast message
        while (socket.isConnected()) {
            try {
                message = in.readLine();

                if (message == null) {
                    closeEverything(socket, in, out);
                }

                System.out.println(message);

            } catch (IOException e) {
                System.out.println("Connection lost");
                closeEverything(socket, in, out);
                break;
            }
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

            handleCommand(message);

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
            default:
                sendToServer(message);
        }
    }

    private void handleQuit() throws IOException {

        sendToServer("/quit");
        System.out.println("Disconnected from server");

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
