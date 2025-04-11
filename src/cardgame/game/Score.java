package cardgame.game;

import java.util.*;
import java.util.stream.Collectors;

import javax.sound.midi.Track;

import cardgame.model.*;

public class Score {

    public Player player;

    // colours array
    ArrayList<String> colours = new ArrayList<>(Arrays.asList(
                "blue", "green", "grey", "purple", "orange", "red"));

    /*
     * method 1: count player cards
     * -> count number of cards based on colour
     * -> update playerCount
     *
     * method 2: find highest number of cards for each number
     * -> use method 2
     * -> compare and find highest number
     * -> update highestCount
     *
     * method 3: calculate score
     * -> if-else
     * -> if respective colour and number matches highest number
     * -> total + 1
     * -> else
     * -> get value of card and add to total
     * method 4 : determine winner
     * -> use method 3 to get player's score
     * -> compare player's score with all other players
     * -> if any other player has a higher score
     * -> return false (not winner)
     * -> else
     * -> return true (winner)
     */


    /*** Method 1: Count Player's Coloured Cards ***/
    public void countPlayerCards(Player p) {

        // reset count for new player
        p.playerColouredCards.clear();

        // use player attribute to get card arraylist
        for (Card card : p.calculateScoreDeck) {

            // use card attribute to get card colour
            String colour = card.getColour();

            // get current count
            // default to 0 if not present
            int count = p.playerColouredCards.getOrDefault(colour, 0);

            // increment the count by one
            p.playerColouredCards.put(colour, count + 1);
        }

        colours.stream().forEach(colour -> p.playerColouredCards.putIfAbsent(colour, 0));

    }

    // Calculate scores for 2 players
    public void twoPlayers(){
        for(String colour : colours){
            int p1colouredCardNumbers = Player.players.get(0).playerColouredCards.get(colour);

            if(p1colouredCardNumbers - Player.players.get(0).playerColouredCards.get(colour) >= 2){
                Player.players.get(0).playerScoreCount += Player.players.get(0).playerColouredCards.get(colour);
    
                Iterator<Card> iterator = Player.players.get(0).calculateScoreDeck.iterator();
                while (iterator.hasNext()) {
                    Card c = iterator.next();
                    if (c != null && colour.equals(c.getColour())) {
                        iterator.remove();
                    }
                }
            }else if(p1colouredCardNumbers - Player.players.get(0).playerColouredCards.get(colour) >= 2){
                Player.players.get(1).playerScoreCount += Player.players.get(0).playerColouredCards.get(colour);
    
                Iterator<Card> iterator = Player.players.get(0).calculateScoreDeck.iterator();
                while (iterator.hasNext()) {
                    Card c = iterator.next();
                    if (c != null && colour.equals(c.getColour())) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    // Calculate scores for 3 players and above
    public void threePlayersAndAbove() {

        for (String colour : colours) {
            //reset tracking for each colour
            List<Player> playersWithHighestNumOfCards = new ArrayList<>();
            int highestCount = -1;
    
            // Step 1: Find highest count for this colour
            for (Player p : Player.players) {
                int currentCount = p.playerColouredCards.getOrDefault(colour, 0);
                if (currentCount > highestCount) {
                    highestCount = currentCount;
                }

            }
            if(highestCount > 0){
                for (Player p : Player.players) {
                    if (p.playerColouredCards.getOrDefault(colour, 0) == highestCount) {
                        playersWithHighestNumOfCards.add(p);
                    }
                }
            }


            for (Player p : playersWithHighestNumOfCards) {
                p.playerScoreCount += highestCount;
    
                Iterator<Card> iterator = p.calculateScoreDeck.iterator();
                while (iterator.hasNext()) {
                    Card c = iterator.next();
                    if (c != null && colour.equals(c.getColour())) {
                        iterator.remove();
                    }
                }
            }
        }
        
    }

    public void calculateScore() {
        // 1. Create deep copies of open decks for calculation
        for (Player p : Player.players) {
            p.calculateScoreDeck = deepCopyCards(p.openDeck);
            countPlayerCards(p);
        }

        // Process the scoring for 3 players and above
        if(Player.players.size() == 2){
            twoPlayers();
        }else{
            threePlayersAndAbove();
        }
        
        for (Player p : Player.players) {
            for(Card c: p.calculateScoreDeck){
                p.playerScoreCount += c.getValue();
            }
        }
        
        for(Player p : Player.players){
            System.out.println(p.name + " score: " + p.playerScoreCount);
        }

    }

    private ArrayList<Card> deepCopyCards(ArrayList<Card> original) {
        ArrayList<Card> copy = new ArrayList<>();
        for (Card c : original) {
            copy.add(new Card(c.getValue(), c.getColour())); // Assuming this constructor works as intended
        }
        return copy;
    }
}
