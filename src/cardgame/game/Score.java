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

        // If colour not found, put value 0 into map
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
            } else if (p1colouredCardNumbers - p2colouredCardNumbers >= 2) {
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

        // Adds all face value cards into the players score
        for (Player p : Player.players) {
            for (Card c : p.calculateScoreDeck) {
                p.playerScoreCount += c.getValue();
            }
        }

        Collections.sort(Player.players, new ScoreCardComparator());

        // Display player's scoring
        List<Player> sortedPlayers = Player.players; // already sorted as described

        String[] labels = { "[WINNER]", "[SECOND]", "[THIRD]", "[FOURTH]", "[FIFTH]", "[SIXTH]" }; // Extend if needed

        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player current = sortedPlayers.get(i);
            String label = (i < labels.length) ? labels[i] : "[" + (i + 1) + "TH]";

            if (i + 1 < sortedPlayers.size()) {
                Player next = sortedPlayers.get(i + 1);

                if (current.playerScoreCount == next.playerScoreCount) {
                    int cardDifference = next.openDeck.size() - current.openDeck.size();
                    System.out.println(label + " " + current.name + " wins " + next.name +
                            " by " + cardDifference + " cards with score " + current.playerScoreCount);
                } else {
                    System.out.println(label + " " + current.name + " wins with score " + current.playerScoreCount);
                }
            } else {
                // Last player (no one to compare to)
                System.out.println(label + " " + current.name + " wins with score " + current.playerScoreCount);
            }
        }

    }
}
