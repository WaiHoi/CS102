import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GameLoop {
    /** 
    2. Draw anonymous card
    3. Determine safe cards
    4. Draw cards that are same color or number into the players deck
    5. Check if any players have a card from each color deck OR when deck is empty. END LOOP**/

    // public boolean checkPlayersHandForCardFromEachColour(){

    //     return false;
    // }
    public int round = 1;
    public int counter = 0;

    public boolean checkPlayersHandForCardFromEachColour(Player p) {
        // Define the required colors
        Set<String> requiredColors = new HashSet<>(Arrays.asList("Red", "Blue", "Green", "Grey", "Purple", "Orange"));

        // Collect colors present in openDeck
        Set<String> foundColors = new HashSet<>();

        for (Card card : p.openDeck) {
            foundColors.add(card.getColour());  // Assuming Card has a getColor() method
        }

        // Return true if all required colors are found
        return foundColors.containsAll(requiredColors);
    }

    public void mainFunction(){
        for (int i = 0; i < player.length; i++) {
            Player p = player.get(i);
            logicalFunction(player.get(i));
            if(checkPlayersHandForCardFromEachColour(p) == false || deck.isEmpty() == true){
                break;
            }
            if(i == player.length){
                i = -1;
                round++;
            }
        }
    }

    public void logicalFunction(Player p){

        Card c = player.anonDeck.get(Math.random() * ((int)(player.anonDeck.size() - 0) + 1));
        c.remove();
        paradeDeck.add(c);

        int safeCards = c.getNumber();

        for (int i = c.getNumber() - 1; i < parade.length; i++) {
            Card currentCard = Card.get(i);
            if(currentCard.getColour().equals(c.getColour()) || currentCard.getNumber() < c.getNumber()){
                c.deck.remove();
                p.add(c);
            }
        }

        
    }

}
