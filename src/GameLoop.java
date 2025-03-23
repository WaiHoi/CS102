import java.util.*;

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
    public ArrayList<Player> players;
    public ArrayList<Card> deck;
    public ArrayList<Card> parade;

    public GameLoop(ArrayList<Player> players, ArrayList<Card> deck, ArrayList<Card> parade) {
        this.players = players;
        this.deck = deck;
        this.parade = parade;
    }

    public boolean checkPlayersHandForCardFromEachColour(Player p) {
        // Define the required colors
        ArrayList<String> requiredColors = new ArrayList<>(Arrays.asList(
            "Red", "Blue", "Green", "Grey", "Purple", "Orange"
        ));
        // Collect colors present in openDeck
        ArrayList<String> foundColors = new ArrayList<>();


        for (Card card : p.openDeck) {
            String color = card.colour;  // Assuming Card has a getColour() method
            if (!foundColors.contains(color)) { // Avoid duplicates
                foundColors.add(color);
            }
        }

        // Return true if all required colors are found
        return foundColors.containsAll(requiredColors);
    }

    public void mainFunction(){
        System.out.println("Round 1");
        String pcards = "";
        for(Card paradeCards: parade){
            pcards = pcards + paradeCards.getColour() + ", " + paradeCards.getValue() + "; ";
        }
        System.out.println(pcards);

        // iterates through the players
        for (int i = 0; i < players.size(); i++) {
    
            //get the first player
            Player p = players.get(i);

            // calls the move of the player
            System.out.println("player " + i);
            logicalFunction(p);

            if(checkPlayersHandForCardFromEachColour(p) == true || deck.isEmpty() == true){
                System.out.println("player " + i);
                break;
            } else if(players.size() == i + 1){
                i = -1;
                round++;
                System.out.println("Round:" + round);
                System.out.println(deck);

            }
        }
    }

    public void logicalFunction(Player p){
        

        Random rand = new Random();
        Card c = p.anonDeck.get(rand.nextInt(p.anonDeck.size())); 
        p.anonDeck.remove(c);
        parade.add(c);

        //add new card for player
        Card newCard = deck.get(0);
        deck.remove(0);
        p.anonDeck.add(newCard);

        System.out.println(c.getColour() + c.getValue());

        String pcards = "";
        for(Card paradeCards: parade){
            pcards = pcards + paradeCards.getColour() + ", " + paradeCards.getValue() + "; ";
        }
        System.out.println(pcards);


        //int safeCards = c.number;

        for (int i = parade.size() - c.number - 2; i >= 0; i--) {
            Card currentCard = parade.get(i);
            if(currentCard.getColour().equals(c.getColour()) || currentCard.number < c.number){
                parade.remove(currentCard);
                p.openDeck.add(currentCard);
            }
        }

        

        //print out open deck cards
        System.out.println(p.toString(p.openDeck));

    }

}
