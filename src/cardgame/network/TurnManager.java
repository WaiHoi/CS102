package cardgame.network;

import java.util.concurrent.atomic.*;

import cardgame.io.output.*;

public class TurnManager {
    private static final AtomicInteger currentPlayerID = new AtomicInteger(1);
    private static boolean networkMode = false; 
    private static int totalHumanPlayers = 0;
    private static int totalPlayers = 0;
    private static GameOutput output;
    private static boolean gameInitialised = false;

    public static void initialize(boolean isNetworkGame, int humans, int bots, GameOutput gameOutput) {
        networkMode = isNetworkGame;
        totalHumanPlayers = humans;
        totalPlayers = humans + bots;

        // console -> use provided output
        // network -> use own output
        if (networkMode) {
            output = null;
        } else {
            output = gameOutput;
        }
    
        // start with player 1
        currentPlayerID.set(1);
        gameInitialised = true;
    
        if (!networkMode && output != null) {
            output.printf("Game started with %d human players and %d bots\n", humans, bots);
        }
    }

    public static int getCurrentPlayerID() {
        return currentPlayerID.get();
    }

    public static void setCurrentPlayer(int playerID) {
        if (playerID > 0 && playerID <= totalHumanPlayers) {
            currentPlayerID.set(playerID);

            broadcastTurnUpdateIfRequired(playerID);
        }
    }

    public static void nextTurn() {
        int currentID = currentPlayerID.get();
        int nextID = (currentID % totalPlayers) + 1;
        currentPlayerID.set(nextID);

        broadcastTurnUpdateIfRequired(nextID);
    }

    private static void broadcastTurnUpdateIfRequired(int playerID) {

        if (!gameInitialised) {
            return;
        }

        if (currentPlayerID.get() == playerID) {
            ClientHandler client = ClientHandler.getHandlerForPlayer(playerID);
            if (client != null && client.getOutput() != null) {
                ((NetworkOutput) client.getOutput()).broadcastTurnUpdate(playerID);
            } else if (output != null) {
                output.printf("Player %d's turn\n", playerID);
            }
        }
        
    }

    public static boolean isMyTurn(int playerID) {
        return currentPlayerID.get() == playerID;
    }

    public static int getTotalHumanPlayers() {
        return totalHumanPlayers;
    }

    public static boolean getNetworkMode() {
        return networkMode;
    }

}