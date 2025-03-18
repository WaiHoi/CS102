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



    while (deck.isEmpty() == false){
        
        
        if(checkPlayersHandForCardFromEachColour() == false){
            break;
        }
    }

}
