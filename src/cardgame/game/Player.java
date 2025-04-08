package cardgame.game;

import java.util.ArrayList;
import java.util.Random;
import java.util.Collections;

import cardgame.model.Card;

public abstract class Player {
    public ArrayList<Card> closedDeck = new ArrayList<>();
    public ArrayList<Card> openDeck = new ArrayList<>();
    public static ArrayList<Player> players = new ArrayList<>();

    public String name;
    public int playerID;

    // Constructor with parameter to set if the player is a bot
    public Player(String name, int playerID) {
        this.name = name;
        this.playerID = playerID;
    }

    public ArrayList<Card> getAnonDeck() {
        return closedDeck;
    }

    public ArrayList<Card> getOpenDeck() {
        return openDeck;
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public String getPlayerName() {
        return name;
    }

    public int getPlayerID() {
        return playerID;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public static void randomizePlayers(){
        Random rand = new Random();
        int rotateValue = rand.nextInt(Player.players.size());
        Collections.rotate(players, rotateValue);
    }

    public abstract int placeCard();
    public abstract void lastRound(Player p);
    
    
}
