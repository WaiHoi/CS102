package cardgame.game;

import java.util.*;
import java.util.stream.Collectors;

import cardgame.model.*;
import cardgame.utility.*;

import com.github.lalyos.jfiglet.FigletFont;

public class Score {

    // colours array
    ArrayList<String> colours = new ArrayList<>(Arrays.asList(
            "blue", "green", "grey", "purple", "orange", "red"));

    /*
     * Count Player's Coloured Cards
     */
    public void countPlayerCards(Player p) {

        // reset count for new player

        p.playerColouredCards.clear();

        for (Card card : p.calculateScoreDeck) {

            String colour = card.getColour();

            // Get number of cards of certain colour from player, if key not found, return 0
            int count = p.playerColouredCards.getOrDefault(colour, 0);

            // increment 1 to count if colour is found
            p.playerColouredCards.put(colour, count + 1);
        }

        // If missing colourm, add it with a value of 0
        // This avoids null checks later during score comparison
        colours.stream().forEach(colour -> p.playerColouredCards.putIfAbsent(colour, 0));
    }

    /*
     * Deep copy of player's opendeck Cards array;
     * Deep copy - Create another copy of seperate object of cards in another array
     */
    private ArrayList<Card> deepCopyCards(ArrayList<Card> original) {
        ArrayList<Card> copy = new ArrayList<>();

        // Copy cards and create new Card objects
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

            // Initialize: no player selected for penalty yet
            int playerToDeduct = -1;

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

                int currentScore = Player.players.get(playerToDeduct).getPlayerScore();
                int pointsToAdd = Player.players.get(playerToDeduct).playerColouredCards.get(colour);

                // add points to players based on how many colour cards they have
                Player.players.get(playerToDeduct).setPlayerScore(currentScore + pointsToAdd);
                

                // Then remove all cards of that colour from their scoring deck
                Player.players
                        .get(playerToDeduct).calculateScoreDeck = Player.players.get(playerToDeduct).calculateScoreDeck
                                .stream()
                                .filter(c -> c != null && !colour.equals(c.getColour()))
                                .collect(Collectors.toCollection(ArrayList::new));
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
            // Step 4: Remove the coloured cards of this colour from the calculating score
            // deck
            for (Player p : playersWithHighestNumOfCards) {
                p.setPlayerScore(p.getPlayerScore() + highestCount);

                p.calculateScoreDeck = p.calculateScoreDeck.stream()
                        .filter(c -> c != null && !colour.equals(c.getColour()))
                        .collect(Collectors.toCollection(ArrayList::new));

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
                p.setPlayerScore(p.getPlayerScore() + c.getValue());
            }
        }

        // Sort players by score and number of cards (lower is better in Parade)
        Collections.sort(Player.players, new ScoreCardComparator());
        List<Player> sortedPlayers = Player.players;

        if (!sortedPlayers.isEmpty()) {
            Player winner = sortedPlayers.get(0);

            // Check for tie with second place
            if (sortedPlayers.size() > 1 && winner.playerScoreCount == sortedPlayers.get(1).playerScoreCount) {
                Player second = sortedPlayers.get(1);
                int cardDifference = winner.openDeck.size() - second.openDeck.size();

                try {
                    // Generate Figlet for "JOINT WINNERS" in yellow
                    String jointWinnerArt = FigletFont.convertOneLine("JOINT WINNERS");
                    System.out.println(AnsiColors.colorize(jointWinnerArt, AnsiColors.BRIGHT_YELLOW));
                } catch (Exception e) {
                    System.out.println(AnsiColors.colorize("JOINT WINNERS", AnsiColors.BRIGHT_YELLOW));
                }

                // Print names in bold
                System.out.println(
                        AnsiColors.colorizeBold(winner.name, AnsiColors.BRIGHT_YELLOW) + " and " +
                                AnsiColors.colorizeBold(second.name, AnsiColors.BRIGHT_YELLOW) +
                                " tie with score " + winner.playerScoreCount +
                                " (won by " + cardDifference + " cards)");

                // Print remaining players
                for (int i = 2; i < sortedPlayers.size(); i++) {
                    System.out.println(sortedPlayers.get(i).name + " got a score of " +
                            sortedPlayers.get(i).playerScoreCount);
                }
                System.out.println(label + " " + p.getPlayerName() +
                        " got a score of " + p.getPlayerScore());
            } else {
                // Single winner
                try {
                    // Generate Figlet for "WINNER" in yellow
                    String winnerArt = FigletFont.convertOneLine("WINNER");
                    System.out.println(AnsiColors.colorize(winnerArt, AnsiColors.BRIGHT_YELLOW));
                } catch (Exception e) {
                    System.out.println(AnsiColors.colorize("WINNER", AnsiColors.BRIGHT_YELLOW));
                }

                // Print winner name in bold
                System.out.println(
                        AnsiColors.colorizeBold(winner.name, AnsiColors.BRIGHT_YELLOW) + " wins with score " + winner.playerScoreCount);

                // Print other players
                for (int i = 1; i < sortedPlayers.size(); i++) {
                    System.out.println(sortedPlayers.get(i).name + " got a score of " +
                            sortedPlayers.get(i).playerScoreCount);
                }
            }
        }
    }
}
