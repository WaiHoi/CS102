package cardgame.game;

import java.util.*;
import java.io.*;

import cardgame.GameMenu;
import cardgame.model.*;

public class Game {
    public static int currentRound = 1;
    public static ArrayList<Card> deck = new ArrayList<>();
    public static ArrayList<Card> parade = new ArrayList<>();
    public static int lastRound = 0;
    public static Card card;
    public static Player player;

    private static boolean lastRoundTriggered = false; // Flag for last round

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
        Scanner sc = new Scanner(System.in);
        int selectNumber = 0;
        selectNumber = p.placeCard();

        Card c = p.closedDeck.get(selectNumber);
        p.closedDeck.remove(c);
        parade.add(c);

        // if last round, do not add new card
        if (!lastRound) {
            addNewCard(p);
        }

        System.out.println("\nOpening up your card now...");
        System.out.println("You have drawn the card: " + c.getColour() + " " + c.getValue() + "\n");

        // Print current parade
        System.out.println("Parade:\n" + Card.printCards(parade, false, false));

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
        System.out.println("\nCards that you collected this round:");
        System.out.println(Card.printCards(cardsDrawn, false, false));

        // Show open deck
        System.out.println("\nYour deck of cards:");
        System.out.println(Card.printCards(p.openDeck, true, false) + "\n");
    }

    public static void mainFunction() {

        System.out.println("\n----- Round " + currentRound + " -----\n");
        System.out.print("Parade:\n" + Card.printCards(parade, false, false) + "\n");

        while (true) {
            boolean processLastRound = false;
            Player triggeringPlayer = null;

            // Process regular turns
            for (Player p : Player.players) {
                System.out.println("\n" + p.name + "'s turn!\n");

                // Play the current turn (regular or last round)
                gameLogic(p, lastRoundTriggered);

                // Check for last round conditions
                if (!lastRoundTriggered && (checkPlayersHandForCardFromEachColour(p) || deck.isEmpty())) {
                    lastRoundTriggered = true;
                    triggeringPlayer = p;
                    System.out.println("Last round triggered by " + p.name + "!");
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
                System.out.println("\n----- Round " + currentRound + " -----\n");
            }
        }

        // Game over logic
        System.out.println("\n=== GAME OVER ===");
        System.out.println("Total rounds played: " + currentRound);
    }

    private static void executeLastRound() {
        System.out.println("\n----- FINAL ROUND -----\n");

        // Give each player one final turn
        for (Player p : Player.players) {
            System.out.println("\n" + p.name + "'s final turn!\n");
            gameLogic(p, lastRoundTriggered);
            p.lastRound(p);
        }
    }
}
