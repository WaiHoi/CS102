package cardgame.game;

import java.util.*;

import cardgame.model.*;

public abstract class Player {

    private String name;
    private int playerID;
    private int playerScore;

    protected ArrayList<Card> closedDeck = new ArrayList<>();
    protected ArrayList<Card> openDeck = new ArrayList<>();

    public ArrayList<Card> calculateScoreDeck = new ArrayList<>(); //deep copy of open deck, only used in final score calculation
    public TreeMap<String, Integer> playerColouredCards = new TreeMap<>();
    public static ArrayList<Player> players = new ArrayList<>();
    

    // Constructor with parameter to set if the player is a bot
    public Player(String name, int playerID) {
        this.name = name;
        this.playerID = playerID;
    }

    public ArrayList<Card> getClosedDeck() {
        return closedDeck;
    }

    public void setClosedDeck(ArrayList<Card> closedDeck) {
        this.closedDeck = closedDeck;
    }

    public ArrayList<Card> getOpenDeck() {
        return openDeck;
    }

    public void setOpenDeck(ArrayList<Card> openDeck) {
        this.openDeck = openDeck;
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

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public void addToClosedDeck(Card card) {
        closedDeck.add(card);
    }
    
    public Card getCardFromClosedDeck(int index) {
        if (index >= 0 && index < closedDeck.size()) {
            return closedDeck.get(index);
        }
        return null;
    }

    public void removeCardFromClosedDeck(Card card) {
        closedDeck.remove(card);
    }
    
    public void addToOpenDeck(Card card) {
        openDeck.add(card);
    }

    public abstract int placeCard();
    public abstract void lastRound(Player p);
    public abstract int placeCardLastRound(int listSize);
    
    
}
