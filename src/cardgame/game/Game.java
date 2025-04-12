package cardgame.game;

import java.util.*;

import cardgame.model.*;
import cardgame.utility.UsernameValidator;

public class Game {
    public static int currentRound = 1;
    public static ArrayList<Card> deck = new ArrayList<>(); //the pile that players draw from 
    public static ArrayList<Card> parade = new ArrayList<>(); //actual line of cards that the players lay out in the middle
    public static int lastRound = 0;
    public static Card card;
    public static Player player;
    public static Score score = new Score();

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

        // Ask the player to choose which card to play (e.g. lowest risk card if it's a bot)
        int selectNumber = p.placeCard();
    
        // Take the selected card from the player's closed hand
        Card c = p.closedDeck.get(selectNumber);
    
        // Remove that card from their hand (closedDeck)
        p.closedDeck.remove(c);
    
        // Add the played card to the end of the parade line
        parade.add(c);
    
        // If it’s not the final round, let the player draw 1 replacement card
        if (!lastRound) {
            addNewCard(p); // refill hand after playing a card
        }
    
        // Reveal what card the player just played
        System.out.println("\nOpening up " + p.getPlayerName() + "'s card now...");
        System.out.println(p.getPlayerName() + " has drawn the card: " + c.getColour() + " " + c.getValue() + "\n");
    
        // Track which parade cards will be collected this round
        ArrayList<Card> cardsDrawn = new ArrayList<>();
    
        // Simulate the Parade rule: If I play this card, which earlier cards will I collect?
        // → Skip 'c.getValue()' number of cards, then check all cards before that
        for (int i = parade.size() - c.getValue() - 2; i >= 0; i--) {
    
            // If index goes negative, break to avoid out-of-bounds error
            if (i < 0)
                break;
    
            // Check each card in the parade that may be collectible
            Card currentCard = parade.get(i);
    
            // If it matches the played card’s colour or is less than or equal in value, I must collect it
            if (currentCard.getColour().equals(c.getColour()) || currentCard.getValue() <= c.getValue()) {
    
                // Remove collectible card from the parade
                parade.remove(currentCard);
    
                // Add it to the player’s open deck (face-up scoring pile)
                p.openDeck.add(currentCard);
    
                // Also track it in this round’s collected cards (for printing)
                cardsDrawn.add(currentCard);
            }
        }
    
        // Show the updated parade after card was added + any cards collected
        System.out.println("Updated Parade:\n");
        Card.printCards(parade, false, true, true); // prints parade as ASCII art with line labels
    
        // Show what cards the player collected this round
        System.out.println("\nCards that " + p.getPlayerName() + " has collected this round:");
        Card.printCards(cardsDrawn, false, true, true); // show collected cards
    
        // Show the player’s open scoring deck after collecting
        System.out.println("\n" + p.getPlayerName() + "'s deck of cards:");
        Card.printCards(p.openDeck, true, true, true); // show total collected cards
    
        // Pause the game so the next player doesn’t start immediately
        Scanner sc = new Scanner(System.in);
        System.out.print("\nPress Enter to continue> ");
        sc.nextLine();
    }
    

    public static void mainFunction() {

        // At the start of a new game, the last round hasn't been triggered
        lastRoundTriggered = false;
    
        // Display round header (e.g., "Round 1")
        displayRoundHeader(currentRound);
    
        // Main game loop — continues until the game ends
        while (true) {
    
            // Loop through each player for their turn
            for (Player currentPlayer : Player.players) {
    
                // Display whose turn it is
                System.out.println("\n--------------------------");
                System.out.println("    " + currentPlayer.name + "'s turn!    ");
                System.out.println("--------------------------\n");
    
                // Show the current parade cards
                System.out.println("Parade:\n");
                Card.printCards(parade, false, true, true); 
                // Parameters: (playerCardOptions = false, displayCardOptions = true, isParade = true)
    
                // Execute the player's turn (handles regular and last round logic)
                gameLogic(currentPlayer, lastRoundTriggered);
    
                // Check if last round should be triggered
                if (!lastRoundTriggered &&
                    (checkPlayersHandForCardFromEachColour(currentPlayer) || deck.isEmpty())) {
    
                    // Trigger last round conditions
                    lastRoundTriggered = true;
                    System.out.println("Last round triggered by " + currentPlayer.name + "!\n");
    
                    // Let all other players finish their final turns
                    executeLastRound(currentPlayer);
    
                    // End the game
                    return;
                }
            }
    
            // Increase round counter after all players have taken a turn
            currentRound++;
            displayRoundHeader(currentRound);
        }
    }
    

    private static void executeLastRound(Player triggeringPlayer) {
        System.out.println("\n+-------------------------+");
        System.out.println("|       FINAL ROUND       |");
        System.out.println("+-------------------------+");

        // get player who triggered last round
        int startIndex = Player.players.indexOf(triggeringPlayer);
        List<Player> finalRoundOrder = new ArrayList<>();

        // add players from triggering player to end
        for (int i = startIndex; i < Player.players.size(); i++) {
            finalRoundOrder.add(Player.players.get(i));
        }

        // add players from start to triggering player
        for (int i = 0; i < startIndex; i++) {
            finalRoundOrder.add(Player.players.get(i));
        }

        for (Player p : finalRoundOrder) {
            System.out.println("\n-------------------------------");
            System.out.println("    " + p.name + "'s final turn!    ");
            System.out.println("-------------------------------\n");
    
            // Display parade as ASCII cards
            System.out.println("\nParade:\n");
            Card.printCards(parade, false, true, true); // displayCardOptions = true, lineNumber = true
    
            gameLogic(p, true);
            p.lastRound(p);
        }

        score.calculateScore();
    }

    private static void showGameOver() {
        System.out.println("+----------------------+");
        System.out.println("|      GAME OVER       |");
        System.out.println("+----------------------+");

        Score score = new Score();
        System.out.println("Total rounds played: " + currentRound);
    }

    public static void displayRoundHeader(int currentRound) {
        System.out.println("\n+----------------------+");
        System.out.printf ("|       ROUND %-2d       |\n", currentRound);
        System.out.println("+----------------------+");
    }

    public static void resetGame() {
        // Reset all static variables
        currentRound = 1;
        deck.clear();
        parade.clear();
        lastRound = 0;
        score = new Score();
        lastRoundTriggered = false;

        for (Player p : Player.players) {
            p.openDeck.clear();
            p.closedDeck.clear();
        }

        // clear all players from last game
        Player.players.clear();
        // clear previous usernames
        UsernameValidator.clearAllUsernames();

    }

}
