package cardgame.network;

import java.util.concurrent.atomic.*;

import cardgame.io.output.NetworkOutput;

public class TurnManager {
    private static final AtomicInteger currentPlayerID = new AtomicInteger(1);
    private static boolean networkMode = false; 
    private static int totalHumanPlayers = 0;
    private static NetworkOutput networkOutput;

    public static void initialize(boolean isNetworkGame, int humans, int bots, NetworkOutput output) {
        networkMode = isNetworkGame;
        totalHumanPlayers = humans;
        networkOutput = output;

        // start with player 1
        currentPlayerID.set(1);

        if (networkMode && networkOutput != null) {
            networkOutput.broadcastTurnUpdate(1);
        }
    }

    public static int getCurrentPlayerID() {
        return currentPlayerID.get();
    }

    public static void setCurrentPlayer(int playerID) {
        if (playerID > 0 && playerID <= totalHumanPlayers) {
            currentPlayerID.set(playerID);

            if (networkMode && networkOutput != null) {
                networkOutput.broadcastTurnUpdate(playerID);
            }
        }
    }

    public static void nextTurn() {
        int currentID = currentPlayerID.get();
        int nextID = (currentID % totalHumanPlayers) + 1;
        currentPlayerID.set(nextID);

        if (networkMode && networkOutput != null) {
            networkOutput.broadcastTurnUpdate(nextID);
        }
    }

    public static boolean isMyTurn(int playerID) {
        return currentPlayerID.get() == playerID;
    }

    public static int getTotalHumanPlayers() {
        return totalHumanPlayers;
    }

}