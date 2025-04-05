package cardgame.game;

import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

import cardgame.GameMenu;
import cardgame.model.*;
import cardgame.network.ClientHandler;
import cardgame.network.TurnManager;
import cardgame.io.input.*;
import cardgame.io.output.*;

public class Game {
    public static int currentRound = 1;
    public static ArrayList<Card> deck = new ArrayList<>();
    public static ArrayList<Card> parade = new ArrayList<>();
    public static int lastRound = 0;
    public static Card card;
    public static Player player;

    private static boolean lastRoundTriggered = false; // Flag for last round

    // input and output 
    private static GameInput input;
    private static GameOutput output;
    private static TurnManager turnManager;
    
    public static boolean checkPlayersHandForCardFromEachColour(Player p) {
        // Define the required colors
        ArrayList<String> requiredColors = new ArrayList<>(Arrays.asList(
                "red", "blue", "green", "grey", "purple", "orange"));

        // Collect colors present in the player's openDeck
        ArrayList<String> foundColors = new ArrayList<>();
        for (Card card : p.openDeck) {
            if (!foundColors.contains(card.getColour())) { // Avoid duplicates
                foundColors.add(card.getColour());
            }
        }

        // Check if all required colors are in foundColors
        for (String color : requiredColors) {
            if (!foundColors.contains(color)) {
                return false; // If any required color is missing, return false
            }
        }
        return true; // If all colors are found, return true
    }

    public static void addNewCard(Player p) {
        Card newCard = deck.get(0);
        deck.remove(0);
        p.closedDeck.add(newCard);
    }

    public static void gameLogic(Player p, boolean lastRound) {

        int selectNumber = p.placeCard();

        Card c = p.closedDeck.get(selectNumber);
        p.closedDeck.remove(c);
        parade.add(c);

        // if last round, do not add new card
        if (!lastRound) {
            addNewCard(p);
        }

        output.sendPrivate("\nOpening up your card now...");
        output.sendPrivate("You have drawn the card: " + c.getColour() + " " + c.getValue() + "\n");

        // Print current parade
        output.broadcastToAll("Parade:\n" + Card.printCards(parade, false, false));

        ArrayList<Card> cardsDrawn = new ArrayList<>();

        // Check collectible cards
        for (int i = parade.size() - c.number - 2; i >= 0; i--) {
            if (i < 0)
                break;
            Card currentCard = parade.get(i);

            if (currentCard.getColour().equals(c.getColour()) || currentCard.number <= c.number) {
                parade.remove(currentCard);
                p.openDeck.add(currentCard);

                cardsDrawn.add(currentCard);
            }
        }

        // Show cards drawn in the current round.
        output.sendPrivate("\nCards that you collected this round:");
        output.sendPrivate(Card.printCards(cardsDrawn, false, false));

        // Show open deck
        output.sendPrivate("\nYour deck of cards:");
        output.sendPrivate(Card.printCards(p.openDeck, true, false) + "\n");
    }

    public static void mainFunction() {

        // initialise input and output
        try {
            ClientHandler handler = ClientHandler.getHandlerForPlayer(1);
            input = new NetworkInput(handler, output);
            output = new NetworkOutput(handler);
        } catch (IOException e) {
            System.err.println("Network initialization failed: " + e.getMessage());
        }

        output.broadcastToAll("\n----- Round " + currentRound + " -----\n");
        output.broadcastToAll("Parade:\n" + Card.printCards(parade, false, false) + "\n");

        while (true) {
            boolean processLastRound = false;
            Player triggeringPlayer = null;

            // Process regular turns
            for (Player p : Player.players) {

                // sent to all
                output.broadcastToAll(p.name + "'s turn!\n");
 
                // Play the current turn (regular or last round)
                gameLogic(p, lastRoundTriggered);

                // Check for last round conditions
                if (!lastRoundTriggered && (checkPlayersHandForCardFromEachColour(p) || deck.isEmpty())) {
                    lastRoundTriggered = true;
                    triggeringPlayer = p;
                    output.broadcastToAll("Last round triggered by " + p.name + "!");
                    processLastRound = true;
                }

                // If last round was just triggered, break to process final turns
                if (processLastRound) {
                    break;
                }
            }

            // Process last round if triggered
            if (processLastRound) {
                executeLastRound();
                break; // Exit the game loop after last round
            }

            // Only increment round if not in last round
            if (!lastRoundTriggered) {
                currentRound++;
                output.broadcastToAll("\n----- Round " + currentRound + " -----\n");
            }
        }

        // Game over logic
        output.broadcastToAll("\n=== GAME OVER ===");
        output.broadcastToAll("Total rounds played: " + currentRound);
    }

    private static void determineWinner() {
        Score scoreCalculator = new Score();
        scoreCalculator.calculateScore(Player.players);

        // Sort by score (ascending) and total cards (ascending)
        List<Player> sortedPlayers = Player.players.stream()
            .sorted(Comparator
                .<Player>comparingInt(p -> scoreCalculator.playerScoreCount.get(p))
                .thenComparingInt(Player::getTotalCards))
            .collect(Collectors.toList());

        Player winner = sortedPlayers.get(0);
        int winningScore = scoreCalculator.playerScoreCount.get(winner);

        // Check for ties
        List<Player> tiedWinners = sortedPlayers.stream()
            .filter(p -> scoreCalculator.playerScoreCount.get(p) == winningScore)
            .collect(Collectors.toList());

        // Broadcast results
        output.broadcastToAll("\n=== FINAL SCORES ===");
        Player.players.forEach(p -> output.broadcastToAll(
            String.format("%s: %d points (%d cards)",
                p.name,
                scoreCalculator.playerScoreCount.get(p),
                p.getTotalCards())
        ));

        if (tiedWinners.size() > 1) {
            output.broadcastToAll("\nIt's a tie! Winners:");
            tiedWinners.forEach(p -> output.broadcastToAll("- " + p.name));
        } else {
            output.broadcastToAll("\nWINNER: " + winner.name + " with " + winningScore + " points!");
        }
    }


    private static void executeLastRound() {
        output.broadcastToAll("\n----- FINAL ROUND -----\n");

        // Give each player one final turn
        for (Player p : Player.players) {
            output.broadcastToAll("\n" + p.name + "'s final turn!\n");
            gameLogic(p, lastRoundTriggered);
            p.lastRound(p);
        }

        // Game over logic
        output.broadcastToAll("\n=== GAME OVER ===");
        determineWinner();  // Add this line
    }
}
