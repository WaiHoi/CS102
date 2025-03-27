package cardgame.game;

import java.util.ArrayList;

public abstract class Player {
    public ArrayList<Card> closedDeck = new ArrayList<>();
    public ArrayList<Card> openDeck = new ArrayList<>();
    public static ArrayList<Player> players = new ArrayList<>();

    public String name;

    // Constructor with parameter to set if the player is a bot
    public Player(String name) {
        this.name = name;
    }

    public ArrayList<Card> getAnonDeck() {
        return closedDeck;
    }

    public ArrayList<Card> getOpenDeck() {
        return openDeck;
    }

    public abstract int placeCard();
    
    
}