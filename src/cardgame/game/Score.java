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
     * Calculate scores for 2 players
     */
    public void twoPlayers() {

        // Loop through each colour in the game
        for (String colour : colours) {

            // Initialize: no player selected for penalty yet
            int playerToDeduct = -1;

            // Get how many cards of this colour each player has
            int p1colouredCardNumbers = Player.getPlayers().get(0).getPlayerColouredCards().get(colour);
            int p2colouredCardNumbers = Player.getPlayers().get(1).getPlayerColouredCards().get(colour);

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

                int currentScore = Player.getPlayers().get(playerToDeduct).getPlayerScore();
                int pointsToAdd = Player.getPlayers().get(playerToDeduct).getPlayerColouredCards().get(colour);

                // add points to players based on how many colour cards they have
                Player.getPlayers().get(playerToDeduct).setPlayerScore(currentScore + pointsToAdd);
                

                // Then remove all cards of that colour from their scoring deck
                Player.getPlayers().get(playerToDeduct).removeCardsByColour(colour);
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
            for (Player p : Player.getPlayers()) {
                int currentCount = p.getPlayerColouredCards().getOrDefault(colour, 0);
                if (currentCount > highestCount) {
                    highestCount = currentCount;
                }

            }

            // Step 2: Add all players who have the highest number of cards for this colour
            if (highestCount > 0) {
                for (Player p : Player.getPlayers()) {
                    // Use getOrDefault to avoid nullPointerException if the player has no cards of
                    // this colour
                    if (p.getPlayerColouredCards().getOrDefault(colour, 0) == highestCount) {
                        playersWithHighestNumOfCards.add(p);
                    }
                }
            }

            // Step 3: Add scores to individual players
            // Step 4: Remove the coloured cards of this colour from the calculating score
            // deck
            for (Player p : playersWithHighestNumOfCards) {
                p.setPlayerScore(p.getPlayerScore() + highestCount);

                p.removeCardsByColour(colour);


            }
        }

    }

    /*
     * Calculate Score for players
     */
    public void calculateScore() {

        // Create deep copies of open decks for calculation
        for (Player p : Player.getPlayers()) {
            p.prepareForScoring(p);
        }

        // Process the scoring for 3 players and above, or 2 players
        if (Player.getPlayers().size() == 2) {
            twoPlayers();
        } else {
            threePlayersAndAbove();
        }

        // Add up the face value of all cards in each player's score deck
        for (Player p : Player.getPlayers()) {
            for (Card c : p.getCalculateScoreDeck()) {
                p.setPlayerScore(p.getPlayerScore() + c.getValue());
            }
        }

        // Sort players by score and number of cards (lower is better in Parade)
        Collections.sort(Player.getPlayers(), new ScoreCardComparator());
        List<Player> sortedPlayers = Player.getPlayers();
        String[] labels = { "[WINNER]", "[SECOND]", "[THIRD]", "[FOURTH]", "[FIFTH]", "[SIXTH]" };

        try {
            // Generate Figlet for "WINNER" in yellow
            String winnerArt = FigletFont.convertOneLine("WINNER");
            System.out.println(AnsiColors.colorize(winnerArt, AnsiColors.BRIGHT_YELLOW));
        } catch (Exception e) {
            System.out.println(AnsiColors.colorize("WINNER", AnsiColors.BRIGHT_YELLOW));
        }

        // Get winner (first player in sorted list)
        Player possibleWinner = sortedPlayers.get(0);
        int possibleWinnerScore = possibleWinner.getPlayerScore();
        int possibleWinnerDeckSize = possibleWinner.getOpenDeck().size();

        System.out.println("[WINNER] " + possibleWinner.getPlayerName() + " wins with score " +
                possibleWinnerScore);
        
        // Loop through players
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player p = sortedPlayers.get(i);
            String label = (i < labels.length) ? labels[i] : "[" + (i + 1) + "TH]";

            if (p == possibleWinner) {
                continue;
            }

            // Print tiebreaker note
            if (p.getPlayerScore() == possibleWinnerScore) {
                int cardDiff = p.getOpenDeck().size() - possibleWinnerDeckSize;

                // same score and number of cards
                if (cardDiff == 0) {
                    System.out.println(" -> " + possibleWinner.getPlayerName() + " wins tiebreak against " +
                            p.getPlayerName() + " with equal number of cards (" +
                            possibleWinnerDeckSize + ")");
                    // same score and fewer cards
                } else {
                    System.out.println(" -> " + possibleWinner.getPlayerName() +
                            " wins tiebreak against " + p.getPlayerName() +
                            " by " + cardDiff + " fewer cards (score: " +
                            possibleWinnerScore + ")");
                }
                System.out.println(label + " " + p.getPlayerName() +
                        " got a score of " + p.getPlayerScore());
            } else {
                System.out.println(label + " " + p.getPlayerName() +
                        " got a score of " + p.getPlayerScore());
            }
        }
    }
}
