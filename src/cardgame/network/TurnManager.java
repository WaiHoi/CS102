package cardgame.network;

public class TurnManager {
    private static int currentPlayerID = -1;
    private static boolean networkMode = false; 

    public static void initialize(boolean isNetworkGame) {
        networkMode = isNetworkGame;
    }

    public static int getCurrentPlayerID() {
        return currentPlayerID;
    }

    public static void setCurrentPlayer(int playerID) {
        if (playerID <= 0) {
            return;
        }
        currentPlayerID = playerID;
        if (networkMode && ClientHandler.clients != null) {
            ClientHandler.broadcastTurnUpdate(playerID);
        }
    }

    public static boolean isMyTurn(int playerID) {
        return currentPlayerID == playerID;
    }
}