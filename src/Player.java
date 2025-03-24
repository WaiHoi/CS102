// import java.util.ArrayList;

// public class Player {
//     // Deck of face-down cards (anonymous cards)
//     public ArrayList<Card> anonDeck = new ArrayList<>();
//     // Deck of face-up cards (open cards)
//     public ArrayList<Card> openDeck = new ArrayList<>();

//     // Default constructor
//     public Player() {
//     // No additional setup needed since decks are initialized above
//     }
    
//     // Getter method for the anonymous deck
//     public ArrayList<Card> getAnonDeck() {
//         return anonDeck;
//     }
//     // Getter method for the open deck
//     public ArrayList<Card> getOpenDeck() {
//         return openDeck;
//     }
    
//     // Returns a formatted string representing the given deck (anonDeck or openDeck)
//     public String toString(ArrayList<Card> deckType){

//         // Initialize an empty string to store card descriptions
//         String cards = "";

//         // For-each loop that loops through each card in the provided deck
//         for(Card card : deckType){
//             // Append the card's colour and value to the string
//             cards = cards + card.getColour() + ", " + card.getValue() + "; ";
//         }

//         // If cards were added, format and return the string
//         if (!cards.equals("")){
//             // Remove the trailing "; " and wrap with brackets
//             cards = "[" + cards.substring(0, cards.length() - 2)+ "]";
//         } else {
//             // If the deck is empty, return empty brackets
//             return "[]";
//         }
//         return cards;
//     }
    
// }

import java.util.ArrayList;

public class Player {
    public ArrayList<Card> anonDeck = new ArrayList<>();
    public ArrayList<Card> openDeck = new ArrayList<>();
    public boolean isBot; 

    // Constructor with parameter to set if the player is a bot
    public Player(boolean isBot) {
        this.isBot = isBot;
    }

    public Player() {
        this.isBot = false; // default to human
    }

    public ArrayList<Card> getAnonDeck() {
        return anonDeck;
    }

    public ArrayList<Card> getOpenDeck() {
        return openDeck;
    }

    public String toString(ArrayList<Card> deckType) {
        if (deckType.isEmpty()) {
            return "[]";
        }
    
        // Sort the cards by colour first, then by value
        deckType.sort((card1, card2) -> {
            int colorComparison = card1.getColour().compareTo(card2.getColour());
            return (colorComparison != 0) ? colorComparison : Integer.compare(card1.getValue(), card2.getValue());
        });
    
        // Construct the formatted string
        StringBuilder cards = new StringBuilder("[");
        for (Card card : deckType) {
            cards.append(card.colour).append(", ").append(card.number).append("; ");
        }
    
        // Remove the last "; " and close the bracket
        cards.setLength(cards.length() - 2);
        cards.append("]");
    
        return cards.toString();
    }
    
}