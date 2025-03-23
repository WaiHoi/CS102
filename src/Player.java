import java.util.ArrayList;

public class Player {
    public ArrayList<Card> anonDeck = new ArrayList<>();
    public ArrayList<Card> openDeck = new ArrayList<>();

    public Player() {
        
    }

    public ArrayList<Card> getAnonDeck() {
        return anonDeck;
    }

    public ArrayList<Card> getOpenDeck() {
        return openDeck;
    }

    public String toString(ArrayList<Card> deckType){
        String cards = "";
        for(Card card : deckType){
            cards = cards + card.getColour() + ", " + card.getValue() + "; ";
        }
        if (!cards.equals("")){
            cards = "[" + cards.substring(0, cards.length() - 2)+ "]";
        } else {
            return "[]";
        }
        return cards;
    }
    
}
