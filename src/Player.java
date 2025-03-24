import java.util.ArrayList;

public class Player {
    // Deck of face-down cards (anonymous cards)
    public ArrayList<Card> anonDeck = new ArrayList<>();
    // Deck of face-up cards (open cards)
    public ArrayList<Card> openDeck = new ArrayList<>();

    // Default constructor
    public Player() {
    // No additional setup needed since decks are initialized above
    }
    
    // Getter method for the anonymous deck
    public ArrayList<Card> getAnonDeck() {
        return anonDeck;
    }
    // Getter method for the open deck
    public ArrayList<Card> getOpenDeck() {
        return openDeck;
    }
    
    // Returns a formatted string representing the given deck (anonDeck or openDeck)
    public String toString(ArrayList<Card> deckType){

        // Initialize an empty string to store card descriptions
        String cards = "";

        // For-each loop that loops through each card in the provided deck
        for(Card card : deckType){
            // Append the card's colour and value to the string
            cards = cards + card.getColour() + ", " + card.getValue() + "; ";
        }

        // If cards were added, format and return the string
        if (!cards.equals("")){
            // Remove the trailing "; " and wrap with brackets
            cards = "[" + cards.substring(0, cards.length() - 2)+ "]";
        } else {
            // If the deck is empty, return empty brackets
            return "[]";
        }
        return cards;
    }
    
}
