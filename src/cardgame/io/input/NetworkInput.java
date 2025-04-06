package cardgame.io.input;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.InputMismatchException;

import cardgame.io.output.*;
import cardgame.network.ClientHandler;

public class NetworkInput implements GameInput {
    private final BufferedReader reader;
    private final GameOutput output;
    private final ClientHandler handler;
    private final Object readLock = new Object();

    public NetworkInput(ClientHandler handler, GameOutput output) throws IOException {
        this.handler = handler;
        InputStream is = handler.getSocket().getInputStream();
        this.reader = new BufferedReader(new InputStreamReader(is));
        this.output = output;
    }

    @Override
    // read line
    public String readLine(String prompt) {
        synchronized (readLock) {
            try {
                // send prompt to client
                if (!prompt.isEmpty()) {
                    output.print(prompt);
                }
                String line = reader.readLine();
                System.out.println("[DEBUG] Received input: '" + line + "'");

                // return line
                return line;

            } catch (IOException e) {
                throw new RuntimeException("Network input error", e);
            }
        }
    }

    @Override
    // read number
    public int readInt(String prompt, int min, int max) {

        while (true) {
            try {
                // send prompt to client
                output.print(prompt);

                String input = reader.readLine();
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }

                // error handling
                output.sendError("Input must be between " + min + "and " + max);

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");

            } catch (IOException e) {
                throw new RuntimeException("Network error", e);
            }
        }
    }

}
