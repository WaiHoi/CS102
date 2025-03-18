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


    
}
