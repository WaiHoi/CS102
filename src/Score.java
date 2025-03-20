import java.util.*;

public class Score {
    private HashMap<String, Integer> playerCount;
    private HashMap<String, Integer> highestCount;

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
        playerCount = new HashMap<>();
        highestCount = new HashMap<>();
    }

    /*** Method 1: Count Player's Cards ***/
    public void countPlayerCards(Player player) {

        // reset count for new player 
        playerCount.clear();

        // use player attribute to get card arraylist 
        for (Card card: player.getCards()) {

            // use card attribute to get card colour 
            String colour = card.getColour();

            // get current count 
            // default to 0 if not present 
            int count = playerCount.getOrDefault(colour, 0);

            // increment the count by one
            playerCount.put(colour, count + 1);
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
            
            // count each player's cards
            countPlayerCards(player);

            // checks through all colours  
            for (String colour : colours) {
                // ensures all colours has a count 
                int currentColourCount = playerCount.getOrDefault(colour, 0);
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
    public int calculateScore(Player player) {
        int totalScore = 0;

        // count players cards
        countPlayerCards(player);

        // use player attribute to get card arraylist 
        for (Card card: player.getCards()) {

            // use card attribute to get card colour
            String colour = card.getColour();

            // get number of cards player has
            int playerCardCount = playerCount.getOrDefault(colour, 0);
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

        return totalScore;
    }

    /*** Method 4: Compares the player's score with all other players to determine if they are the winner */
    public boolean isWinner (Player player, ArrayList<Player> playerList) {
        
        // Get the player's score
        int playerScore = calculateScore(player);

        
        // Check each player's score in the list
        for (Player otherPlayer : playerList) {
            
        // Skip comparison with itself
        if (!otherPlayer.equals(player)) {
            int otherScore = calculateScore(otherPlayer);

            // If any other player has a higher score, this player is not the winner
            if (otherScore > playerScore) {
                return false;
            }
        }
    }
        return true;
    }

}

