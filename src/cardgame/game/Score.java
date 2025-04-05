package cardgame.game;

import java.util.*;
import java.util.stream.Collectors;

import javax.sound.midi.Track;

import cardgame.model.*;
import cardgame.io.output.*;

public class Score {
    private HashMap<String, Integer> playerColouredCards;
    private HashMap<String, Integer> highestCount;
    //Track tied majorities
    protected HashMap<Player, Integer> playerScoreCount;
    private HashMap<String, Integer> playersWithMaxCount = new HashMap<>(); 
    private static GameOutput output;


    /* 
    *  method 1: count player cards 
    *             -> count number of cards based on colour 
    *             -> update playerCount 
    *
    *  method 2: find highest number of cards for each number 
    *             -> use method 2  
    *             -> compare and find highest number 
    *             -> update highestCount
    *
    *  method 3: calculate score
    *             -> if-else 
    *             -> if respective colour and number matches highest number
    *                  -> total + 1
    *             -> else 
    *                  -> get value of card and add to total 
    * method 4 : determine winner
    *             -> use method 3 to get player's score
    *             -> compare player's score with all other players
    *             -> if any other player has a higher score
    *                  -> return false (not winner)
    *             -> else
    *                  -> return true (winner)
    */

    public Score() {
        playerColouredCards = new HashMap<>();
        highestCount = new HashMap<>();
        playerScoreCount = new HashMap<>();
    }

    /*** Method 1: Count Player's Coloured Cards ***/
    public void countPlayerCards(Player player) {

        // reset count for new player 
        playerColouredCards.clear();

        // use player attribute to get card arraylist 
        for (Card card: player.getAnonDeck()) {

            // use card attribute to get card colour 
            String colour = card.getColour();

            // get current count 
            // default to 0 if not present 
            int count = playerColouredCards.getOrDefault(colour, 0);

            // increment the count by one
            playerColouredCards.put(colour, count + 1);
        }

    }

    /*** Method 2: Find Highest Number of Cards for Each Colour ***/
    // use arraylist of players -> unsure what class yet 
    public void highestNumberPerColour(ArrayList<Player> playerList) {

        // reset highest count 
        highestCount.clear();

        // colours array
        String[] colours = {"red", "blue", "purple", "green", "grey", "orange"};

        // check through each player
        for (Player player: playerList) {
            
            // count player's cards
            countPlayerCards(player);

            // checks through all colours  
            for (String colour : colours) {
                // ensures all colours has a count 
                int currentColourCount = playerColouredCards.getOrDefault(colour, 0);
                int highestColourCount = highestCount.getOrDefault(colour, 0);

                // update colour count
                if (currentColourCount > highestColourCount) {
                    // replaces count of current colour
                    highestCount.put(colour, currentColourCount);
                }
            }
        }

    }
    
    /*** Method 3: Calculate Score ***/
    public void calculateScore(ArrayList<Player> playerList) {
            determineColorMajorities(playerList);
    
            for (Player player : playerList) {
                countPlayerCards(player);
                int totalScore = 0;
    
                // Face-up cards: sum values
                int faceUp = player.getOpenDeck().stream()
                    .mapToInt(Card::getValue)
                    .sum();
    
                // Face-down points: 1 per card if sole majority
                int faceDown = 0;
            for (String color : playerColouredCards.keySet()) {
                int playerCount = playerColouredCards.get(color);
                int highest = highestCount.getOrDefault(color, 0);
                int maxPlayers = playersWithMaxCount.getOrDefault(color, 0);

                if (playerCount == highest && maxPlayers == 1) {
                    faceDown += playerCount; // 1pt per card if sole majority
                }
            }

            playerScoreCount.put(player, faceUp + faceDown);
        }
    }

    private void determineColorMajorities(ArrayList<Player> players) {
            highestCount.clear();
            playersWithMaxCount.clear();
    
            // Track counts for all players per color
            HashMap<String, HashMap<Player, Integer>> colorCounts = new HashMap<>();
            for (Player player : players) {
                countPlayerCards(player);
                for (String color : playerColouredCards.keySet()) {
                    colorCounts.putIfAbsent(color, new HashMap<>());
                    colorCounts.get(color).put(player, playerColouredCards.get(color));
                }
            }
            // Determine highest count and number of players with that count
        for (String color : colorCounts.keySet()) {
            int max = colorCounts.get(color).values().stream()
                .max(Integer::compare).orElse(0);
            highestCount.put(color, max);

            int tiedPlayers = (int) colorCounts.get(color).values().stream()
                .filter(v -> v == max)
                .count();
            playersWithMaxCount.put(color, tiedPlayers);
        }
    }


    public static void calculateScore(){
        
    }
}

// UML Diagram for Score.java
// +----------------------------+
// |         Score              |
// +----------------------------+
// | - playerCount: HashMap<String, Integer>   |
// | - highestCount: HashMap<String, Integer>  |
// +----------------------------+
// | + Score()                                      |
// | + countPlayerCards(player: Player): void       |
// | + highestNumberPerColour(players: ArrayList<Player>): void |
// | + calculateScore(player: Player): int          |
// | + isWinner(player: Player, players: ArrayList<Player>): boolean |
// +----------------------------+
