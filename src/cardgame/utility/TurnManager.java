package cardgame.utility;

import java.util.concurrent.atomic.*;

public class TurnManager {
    private static final AtomicInteger currentPlayerID = new AtomicInteger(1);
    private static boolean networkMode = false; 
    private static int totalHumanPlayers = 0;
    private static int totalPlayers = 0;
    private static boolean gameInitialised = false;

    public static void initialize(boolean isNetworkGame, int humans, int bots) {
        networkMode = isNetworkGame;
        totalHumanPlayers = humans;
        totalPlayers = humans + bots;
    
        // start with player 1
        currentPlayerID.set(1);
    }

    public static int getCurrentPlayerID() {
        return currentPlayerID.get();
    }

    public static void setCurrentPlayer(int playerID) {
        if (playerID > 0 && playerID <= totalHumanPlayers) {
            currentPlayerID.set(playerID);

        }
    }

    public static void nextTurn() {
        int currentID = currentPlayerID.get();
        int nextID = (currentID % totalPlayers) + 1;
        currentPlayerID.set(nextID);

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