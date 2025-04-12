package cardgame.game;

import java.util.*;

import cardgame.model.*;
import cardgame.utility.*;

public class Score {

    public Player player;

    // colours array
    ArrayList<String> colours = new ArrayList<>(Arrays.asList(
            "blue", "green", "grey", "purple", "orange", "red"));

    /*
     * Count Player's Coloured Cards
     */
    public void countPlayerCards(Player p) {

        // Clear any previous counts so we start fresh for this player
        p.playerColouredCards.clear();

        for (Card card : p.calculateScoreDeck) {

            String colour = card.getColour();

            // Look up how many cards the player already has of this colour
            // If it’s not found in the map, treat it as 0 for now
            int count = p.playerColouredCards.getOrDefault(colour, 0); // returns the integer value associated with the
                                                                       // colour key

            // increment 1 to the count (count + 1) and store it back into the map each time
            // the color is found
            p.playerColouredCards.put(colour, count + 1);
        }

        // Make sure all colours are included in the map
        // Even if a player has 0 of a colour, add it with a value of 0
        // This avoids null checks later during score comparison
        colours.stream().forEach(colour -> p.playerColouredCards.putIfAbsent(colour, 0));
    }

    /*
     * Deep copy of player's opendeck Cards array;
     * Deep copy - Create another copy of seperate object of cards in another array
     */
    private ArrayList<Card> deepCopyCards(ArrayList<Card> original) {
        ArrayList<Card> copy = new ArrayList<>();
        for (Card c : original) {
            copy.add(new Card(c.getValue(), c.getColour()));
        }
        return copy;
    }

    /*
     * Calculate scores for 2 players
     */
    public void twoPlayers() {

        // Loop through each colour in the game
        for (String colour : colours) {

            int playerToDeduct = -1; // Initialize: no player selected for penalty yet

            // Get how many cards of this colour each player has
            int p1colouredCardNumbers = Player.players.get(0).playerColouredCards.get(colour);
            int p2colouredCardNumbers = Player.players.get(1).playerColouredCards.get(colour);

            // If player 1 has 2 or more cards of this colour than player 2, penalize player
            // 1
            if (p1colouredCardNumbers - p2colouredCardNumbers >= 2) {
                playerToDeduct = 0;
            }
            // If player 2 has 2 or more cards of this colour than player 1, penalize player
            // 2
            else if (p2colouredCardNumbers - p1colouredCardNumbers >= 2) {
                playerToDeduct = 1;
            }

            // If there is a player to penalize for lacking by 2
            if (playerToDeduct != -1) {

                Player.players
                        .get(playerToDeduct).playerScoreCount += Player.players
                                .get(playerToDeduct).playerColouredCards.get(colour); // returns the int value mapped to
                                                                                      // the colour key

                // Then remove all cards of that colour from their scoring deck
                Iterator<Card> iterator = Player.players.get(playerToDeduct).calculateScoreDeck.iterator();
                while (iterator.hasNext()) {
                    Card c = iterator.next();

                    // If the card matches the penalized colour, remove it
                    if (c != null && colour.equals(c.getColour())) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    /*
     * Calculate scores for 3 players and above
     */
    public void threePlayersAndAbove() {

        for (String colour : colours) {
            // reset tracking for each colour
            List<Player> playersWithHighestNumOfCards = new ArrayList<>();
            int highestCount = -1;

            // Step 1: Find highest count for this colour
            for (Player p : Player.players) {
                int currentCount = p.playerColouredCards.getOrDefault(colour, 0);
                if (currentCount > highestCount) {
                    highestCount = currentCount;
                }

            }

            // Step 2: Add all players who have the highest number of cards for this colour
            if (highestCount > 0) {
                for (Player p : Player.players) {
                    // Use getOrDefault to avoid nullPointerException if the player has no cards of
                    // this colour
                    if (p.playerColouredCards.getOrDefault(colour, 0) == highestCount) {
                        playersWithHighestNumOfCards.add(p);
                    }
                }
            }

            // Step 3: Add scores to individual players
            // Step 4: Remove the coloured cards of this colour from the calculating score deck
            for (Player p : playersWithHighestNumOfCards) {
                p.playerScoreCount += highestCount;

                Iterator<Card> iterator = p.calculateScoreDeck.iterator();
                while (iterator.hasNext()) {
                    Card c = iterator.next();
                    if (c != null && colour.equals(c.getColour())) { 
                        // checking if the color of card c that we are iterating is of the same color 
                        // as colour (in the big for loop), and is not null
                        iterator.remove(); //remove it if it is
                    }
                }
            }
        }

    }

    /*
     * Calculate Score for players
     */
    public void calculateScore() {

        // Create deep copies of open decks for calculation
        for (Player p : Player.players) {
            p.calculateScoreDeck = deepCopyCards(p.openDeck);
            countPlayerCards(p);
        }

        // Process the scoring for 3 players and above, or 2 players
        if (Player.players.size() == 2) {
            twoPlayers();
        } else {
            threePlayersAndAbove();
        }

        // Add up the face value of all cards in each player's score deck
        for (Player p : Player.players) {
            for (Card c : p.calculateScoreDeck) {
                p.playerScoreCount += c.getValue();
            }
        }

        // Sort players by score and number of cards (lower is better in Parade)
        Collections.sort(Player.players, new ScoreCardComparator());

        List<Player> sortedPlayers = Player.players;
        String[] labels = { "[WINNER]", "[SECOND]", "[THIRD]", "[FOURTH]", "[FIFTH]", "[SIXTH]" };

        // Get winner (first player in sorted list)
        Player possibleWinner = sortedPlayers.get(0);
        int possibleWinnerScore = possibleWinner.playerScoreCount;
        int possibleWinnerDeckSize = possibleWinner.openDeck.size();

        System.out.println("[WINNER] " + possibleWinner.name + " wins with score " + 
                            possibleWinnerScore);

        // Loop through players 
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player p = sortedPlayers.get(i);
            String label = (i < labels.length) ? labels[i] : "[" + (i + 1) + "TH]";

            if (p == possibleWinner) {
                continue;
            }

            // Print tiebreaker note
            if (p.playerScoreCount == possibleWinnerScore) {
                int cardDiff = p.openDeck.size() - possibleWinnerDeckSize;

                // same score and number of cards
                if (cardDiff == 0) {
                    System.out.println(" -> " + possibleWinner.name + " wins tiebreak against " + 
                                        p.name + " with equal number of cards (" + 
                                        possibleWinnerDeckSize + ")");
                // same score and fewer cards
                } else {
                    System.out.println(" -> " + possibleWinner.name + 
                                        " wins tiebreak against " + p.name + 
                                        " by " + cardDiff + " fewer cards (score: " +
                                        possibleWinnerScore + ")");
                }
                System.out.println(label + " " + p.name + 
                                    " got a score of " + p.playerScoreCount);
            } else {
                System.out.println(label + " " + p.name + 
                                    " got a score of " + p.playerScoreCount);
            }
        }
    }
}
