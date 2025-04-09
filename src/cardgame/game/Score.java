package cardgame.game;

import java.util.*;

import cardgame.model.*;

public class Score {
    private HashMap<String, Integer> highestCount;
    private HashMap<Player, Integer> playerScoreCount;

    public Score() {
        highestCount = new HashMap<>();
        playerScoreCount = new HashMap<>();
    }

    /*** Method 1: Count Player's Coloured Cards ***/
    public HashMap<String, Integer> countPlayerCards(Player player) {

        HashMap<String, Integer> playerColouredCards = new HashMap<>();

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
        return playerColouredCards;
    }

    /*** Method 2: Find Highest Number of Cards for Each Colour ***/
    // use arraylist of players -> unsure what class yet 
    public void highestNumberPerColour(ArrayList<Player> playerList) {

        // reset highest count 
        highestCount.clear();

        // check through each player
        for (Player player: playerList) {
            
            // count player's cards
            HashMap<String, Integer> playerCardCount = countPlayerCards(player);

            // checks through all colours  
            for (Map.Entry<String, Integer> entry : playerCardCount.entrySet()) {
                // ensures all colours has a count 
                String colour = entry.getKey();
                int count = entry.getValue();
                highestCount.put(colour, Math.max(highestCount.getOrDefault(colour, 0), count));
            }
        }

    }

    /*** Method 3: Calculate score and find winner */
    public void calculateScore(ArrayList<Player> playerList) {
        playerScoreCount.clear();
        highestNumberPerColour(playerList);

        // count players cards
        for (Player player : playerList) {
            HashMap<String, Integer> playerCardCount = countPlayerCards(player);
            int totalScore = 0;

            // use player attribute to get card arraylist 
            for (Card card: player.getAnonDeck()) {

                // use card attribute to get card colour
                String colour = card.getColour();

                // get number of cards player has
                int playerCount = playerCardCount.getOrDefault(colour, 0);
                // get highest count for that card 
                int highestColourCount = highestCount.getOrDefault(colour, 0);

                // for each card add score accordingly
                if (playerCount == highestColourCount) {
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
    public Player determineWinner(ArrayList<Player> playerList) {
        
        calculateScore(playerList);
        Player winner = null;
        int minScore = Integer.MAX_VALUE;

        for (Player player : playerList) {
            int score = playerScoreCount.getOrDefault(player, 0);

            if (score < minScore) {
                minScore = score;
                winner = player;
            } else if (score == minScore) {
                // tie breaker: fewer cards in AnonDeck
                if (player.getAnonDeck().size() < winner.getAnonDeck().size()) {
                    winner = player;
                }
            }
        }
        return winner;
    }

    public Map<Player, Integer> getPlayerScoreCount() {
        return playerScoreCount;
    }

    public int getPlayerScore(Player player) {
        return playerScoreCount.getOrDefault(player, 0);
    }
}
