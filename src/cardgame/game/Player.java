package cardgame.game;

import java.util.*;
import java.util.stream.Collectors;

import cardgame.model.*;

public abstract class Player {

    private String name;
    private int playerID;
    private int playerScore;
    private ArrayList<Card> calculateScoreDeck = new ArrayList<>(); //deep copy of open deck, only used in final score calculation
    private TreeMap<String, Integer> playerColouredCards = new TreeMap<>();
    private static ArrayList<Player> players = new ArrayList<>();

    protected ArrayList<Card> closedDeck = new ArrayList<>();
    protected ArrayList<Card> openDeck = new ArrayList<>();

    ArrayList<String> colours = new ArrayList<>(Arrays.asList(
            "blue", "green", "grey", "purple", "orange", "red"));


    // Constructor with parameter to set if the player is a bot
    public Player(String name, int playerID) {
        this.name = name;
        this.playerID = playerID;
    }
            
    public ArrayList<Card> getCalculateScoreDeck() {
        return calculateScoreDeck;
    }

    public void setCalculateScoreDeck(ArrayList<Card> calculateScoreDeck) {
        this.calculateScoreDeck = calculateScoreDeck;
    }

    public TreeMap<String, Integer> getPlayerColouredCards() {
        return playerColouredCards;
    }

    public void setPlayerColouredCards(TreeMap<String, Integer> playerColouredCards) {
        this.playerColouredCards = playerColouredCards;
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

    public int getPlayerScore() {
        return playerScore;
    }

    public void setPlayerScore(int playerScore) {
        this.playerScore = playerScore;
    }

    public static void randomizePlayers(){
        Random rand = new Random();
        int rotateValue = rand.nextInt(Player.players.size());
        Collections.rotate(players, rotateValue);
    }

    public void addToClosedDeck(Card card) {
        closedDeck.add(card);
    }

    /*
     * Deep copy of player's opendeck Cards array;
     * Deep copy - Create another copy of seperate object of cards in another array
     */
    private ArrayList<Card> deepCopyCards(ArrayList<Card> original) {
        return original.stream()
       .map(card -> new Card(card.getValue(), card.getColour()))
       .collect(Collectors.toCollection(ArrayList::new));
    }

    public void prepareForScoring(Player p) {
        this.calculateScoreDeck = deepCopyCards(this.openDeck);
        this.countPlayerCards(p);  // Now an internal method
    }

    /*
     * Count Player's Coloured Cards
     */
    public void countPlayerCards(Player p) {

        // reset count for new player
        p.getPlayerColouredCards().clear();

        for (Card card : p.getCalculateScoreDeck()) {

            String colour = card.getColour();

            // Get number of cards of certain colour from player, if key not found, return 0
            int count = p.getPlayerColouredCards().getOrDefault(colour, 0);

            // increment 1 to count if colour is found
            p.getPlayerColouredCards().put(colour, count + 1);
        }

        // If missing colourm, add it with a value of 0
        // This avoids null checks later during score comparison
        colours.stream().forEach(colour -> p.getPlayerColouredCards().putIfAbsent(colour, 0));
    }
    

    public void removeCardsByColour(String colourToRemove) {
        Objects.requireNonNull(colourToRemove, "Colour cannot be null");
        calculateScoreDeck.removeIf(card -> 
            card != null && colourToRemove.equals(card.getColour())
        );
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
