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
        // If the player already had colour counts stored, we wipe it now
        p.playerColouredCards.clear();
    
        // Loop through every card in the player's score deck
        for (Card card : p.calculateScoreDeck) {
    
            // Get the colour of the current card
            String colour = card.getColour();
    
            // Look up how many cards the player already has of this colour
            // If it’s not found in the map, treat it as 0 for now
            int count = p.playerColouredCards.getOrDefault(colour, 0);
    
            // Add 1 to the count and store it back into the map
            // This means we’ve now seen one more card of this colour
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
        for (String colour : colours) {
            int playerToDeduct = -1;

            int p1colouredCardNumbers = Player.players.get(0).playerColouredCards.get(colour);
            int p2colouredCardNumbers = Player.players.get(1).playerColouredCards.get(colour);

            if (p1colouredCardNumbers - p2colouredCardNumbers >= 2) {
                playerToDeduct = 0;
            } else if (p2colouredCardNumbers - p1colouredCardNumbers >= 2) {
                playerToDeduct = 1;
            }

            if (playerToDeduct != -1) {
                Player.players
                        .get(playerToDeduct).playerScoreCount += Player.players.get(playerToDeduct).playerColouredCards
                                .get(colour);

                Iterator<Card> iterator = Player.players.get(playerToDeduct).calculateScoreDeck.iterator();
                while (iterator.hasNext()) {
                    Card c = iterator.next();
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

            // Step 2: Add in all the players that have the highest count
            if (highestCount > 0) {
                for (Player p : Player.players) {
                    if (p.playerColouredCards.getOrDefault(colour, 0) == highestCount) {
                        playersWithHighestNumOfCards.add(p);
                    }
                }
            }

            // Step 3: Add scores to individual players
            // Step 3: Remove the coloured cards from the calculating score deck
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

    /*
     * Calculate Score for players
     */
    public void calculateScore() {

        // For every player, make a deep copy of their openDeck
        // This is used for scoring without modifying their actual deck
        for (Player p : Player.players) {
            p.calculateScoreDeck = deepCopyCards(p.openDeck);
    
            // Count how many cards the player has for each colour
            // Used later for majority bonus or penalty calculations
            countPlayerCards(p);
        }
    
        // Apply different scoring logic based on player count
        // Two players use one scoring rule, three or more use another
        if (Player.players.size() == 2) {
            twoPlayers();
        } else {
            threePlayersAndAbove();
        }
    
        // Add up the face value of all cards in each player's score deck
        // This gives the final numeric score for each player
        for (Player p : Player.players) {
            for (Card c : p.calculateScoreDeck) {
                p.playerScoreCount += c.getValue();
            }
        }
    
        // Sort players by score (lower score is better in Parade)
        Collections.sort(Player.players, new ScoreCardComparator());
    
        // Prepare labels like [WINNER], [SECOND], etc. for displaying rankings
        List<Player> sortedPlayers = Player.players;
        String[] labels = { "[WINNER]", "[SECOND]", "[THIRD]", "[FOURTH]", "[FIFTH]", "[SIXTH]" };
    
        // Loop through each player in ranking order
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player current = sortedPlayers.get(i);
            String label = (i < labels.length) ? labels[i] : "[" + (i + 1) + "TH]";
    
            // Compare current player to the next player, if one exists
            if (i + 1 < sortedPlayers.size()) {
                Player next = sortedPlayers.get(i + 1);
    
                // If both players have the same score, resolve tie using number of cards
                if (current.playerScoreCount == next.playerScoreCount) {
                    int cardDifference = current.openDeck.size() - next.openDeck.size();
    
                    System.out.println(label + " " + current.name + " wins " + next.name +
                            " by " + cardDifference + " cards with score " + current.playerScoreCount);
                } else {
                    // Normal case: current player has a better score than the next
                    System.out.println(label + " " + current.name + " wins with score " + current.playerScoreCount);
                }
            } else {
                // Last player in the list (no one to compare against)
                System.out.println(label + " " + current.name + " wins with score " + current.playerScoreCount);
            }
        }
    }    
}
