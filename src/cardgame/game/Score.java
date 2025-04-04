package cardgame.game;

import java.util.*;
import cardgame.model.*;
import cardgame.io.output.*;

public class Score {
    private HashMap<String, Integer> playerColouredCards;
    private HashMap<String, Integer> highestCount;
    private HashMap<Player, Integer> playerScoreCount;
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

    /*** Method 3: Calculate score and find winner */
    public void calculateScore(ArrayList<Player> playerList, Game game) {


        highestNumberPerColour(Player.players);

        // count players cards
        for (Player player : playerList) {
            countPlayerCards(player);
            int totalScore = 0;

            // use player attribute to get card arraylist 
            for (Card card: player.getAnonDeck()) {

                // use card attribute to get card colour
                String colour = card.getColour();

                // get number of cards player has
                int playerCardCount = playerColouredCards.getOrDefault(colour, 0);
                // get highest count for that card 
                int highestColourCount = highestCount.getOrDefault(colour, 0);

                // for each card add score accordingly
                if (playerCardCount == highestColourCount) {
                    // add one if matches highest count
                    totalScore += 1;
                } else {
                    // add value of card
                    totalScore += card.getValue();
                }
            }
            playerScoreCount.put(player, totalScore);

        }

    }

    /*** Method 4: Compares the player's score with all other players to determine if they are the winner */
    public void isWinner (ArrayList<Player> playerList, Game game) {
        
        Player winner = null;
        int winnerScore = 0;

        // Get the player's score
        calculateScore(playerList, game);

        List<Map.Entry<Player, Integer>> entryList = new ArrayList<>(playerScoreCount.entrySet());

        entryList.sort(Map.Entry.comparingByValue());

        for (int i = 0; i < entryList.size() - 1; i++) {
            Map.Entry<Player, Integer> current = entryList.get(i);
            Map.Entry<Player, Integer> next = entryList.get(i + 1);

            if (current.getValue().equals(next.getValue())) {
                Player player1 = current.getKey();
                Player player2 = next.getKey();

                int p1TotalCards = player1.getAnonDeck().size();
                int p2TotalCards = player2.getAnonDeck().size();

                if (p1TotalCards < p2TotalCards) {
                    winner = player1;
                    winnerScore = current.getValue();
                } else {
                    winner = player2;
                }
            }
        }

        output.broadcastToAll(winner + " has won with a score of " + winnerScore);

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
