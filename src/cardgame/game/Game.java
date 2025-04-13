package cardgame.game;

import java.util.*;

import cardgame.model.*;
import cardgame.utility.UsernameValidator;
import com.github.lalyos.jfiglet.FigletFont;

public class Game {
    private static int currentRound = 1;
    private static ArrayList<Card> deck = new ArrayList<>(); // the pile that players draw from
    private static ArrayList<Card> parade = new ArrayList<>(); // actual line of cards that the players lay out in the
                                                               // middle
    private static Score score = new Score();
    private static boolean lastRoundTriggered = false; // Flag for last round

    public static ArrayList<Card> getDeck() {
        return deck;
    }

    public static void setDeck(ArrayList<Card> deck) {
        Game.deck = deck;
    }

    public static ArrayList<Card> getParade() {
        return parade;
    }

    public static void setParade(ArrayList<Card> parade) {
        Game.parade = parade;
    }

    public static boolean checkPlayersHandForCardFromEachColour(Player p) {

        // First, list all the colours that a player needs to have at least one of
        // If the player has at least one card of each of these 6 colours, they meet the
        // condition.
        ArrayList<String> requiredColors = new ArrayList<>(Arrays.asList(
                "red", "blue", "green", "grey", "purple", "orange"));

        // Create a list to record what colours the player currently has in their open
        // deck
        ArrayList<String> foundColors = new ArrayList<>();

        // Check each card the player has collected
        for (Card card : p.getOpenDeck()) {

            // If we haven’t already recorded this card’s colour, add it to our list.
            if (!foundColors.contains(card.getColour())) {
                foundColors.add(card.getColour());
            }
        }

        // Now go through the full set of required colours
        for (String color : requiredColors) {

            // If even one required colour is missing from the player’s deck, they haven’t
            // met the condition.
            if (!foundColors.contains(color)) {
                return false;
            }
        }

        // If we found all 6 colours in the player’s cards, return true — they meet the
        // requirement.
        return true;
    }

    public static void addNewCard(Player p) {
        Card newCard = deck.get(0); // gets the card at the top of the deck pile
        deck.remove(0);
        p.addToClosedDeck(newCard);
    }

    public static void gameLogic(Player p, boolean lastRound) {

        // Ask the player to choose which card to play (e.g. lowest risk card if it's a
        // bot)
        int selectNumber = p.placeCard();

        // Take the selected card from the player's closed hand
        Card c = p.getCardFromClosedDeck(selectNumber);

        // Remove that card from their hand (closedDeck)
        p.removeCardFromClosedDeck(c);

        // Add the played card to the end of the parade line
        parade.add(c);

        // If it’s not the final round, let the player draw 1 replacement card into his
        // closedDeck
        if (!lastRound) {
            addNewCard(p); // refill hand after playing a card
        }

        // Reveal what card the player just played
        System.out.println("\nOpening up " + p.getPlayerName() + "'s card now...");
        System.out.println(p.getPlayerName() + " has drawn the card: " + c.getColour() + " " + c.getValue() + "\n");

        // Track which parade cards will be collected this round
        ArrayList<Card> cardsDrawn = new ArrayList<>();

        // Simulate the Parade rule: If I play this card, which earlier cards will I
        // collect?
        // → Skip 'c.getValue()' number of cards, then check all cards before that
        for (int i = parade.size() - c.getValue() - 2; i >= 0; i--) { // -1, index of the new card, -1 iterete to before
                                                                      // the skipped zone

            // If index goes negative, break to avoid out-of-bounds error
            if (i < 0)
                break;

            // Check each card in the parade that may be collectible
            Card currentCard = parade.get(i);

            // If it matches the played card’s colour or is less than or equal in value, I
            // must collect it
            if (currentCard.getColour().equals(c.getColour()) || currentCard.getValue() <= c.getValue()) {

                // Remove collectible card from the parade
                parade.remove(currentCard);

                // Add it to the player’s open deck (face-up scoring pile)
                p.addToOpenDeck(currentCard);

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
        Card.printCards(p.getOpenDeck(), true, true, true); // show total collected cards

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
            for (Player currentPlayer : Player.getPlayers()) {

                // Display whose turn it is
                System.out.println("\n--------------------------");
                System.out.println("    " + currentPlayer.getPlayerName() + "'s turn!    ");
                System.out.println("--------------------------\n");

                // Show the current parade cards
                System.out.println("Parade:\n");
                Card.printCards(parade, false, true, true);
                // Parameters: (playerCardOptions = false, displayCardOptions = true, isParade =
                // true)

                // Execute the player's turn (handles regular and last round logic)
                gameLogic(currentPlayer, lastRoundTriggered);

                // Check if last round should be triggered
                if (!lastRoundTriggered &&
                        (checkPlayersHandForCardFromEachColour(currentPlayer) || deck.isEmpty())) {

                    // Trigger last round conditions
                    lastRoundTriggered = true;
                    System.out.println("Last round triggered by " + currentPlayer.getPlayerName() + "!\n");

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
        try {
            // Generate Figlet-style ASCII art for "FINAL ROUND"
            String finalRoundArt = FigletFont.convertOneLine("FINAL ROUND");
            // Wrap the ASCII art in purple ANSI color codes
            System.out.println("\033[95m" + finalRoundArt + "\033[0m");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }

        // Get player who triggered last round
        int startIndex = Player.getPlayers().indexOf(triggeringPlayer);
        List<Player> finalRoundOrder = new ArrayList<>();

        // Add players from triggering player to end
        for (int i = startIndex; i < Player.getPlayers().size(); i++) {
            finalRoundOrder.add(Player.getPlayers().get(i));
        }

        // Then wrap around: add the remaining players from the start up to (but not
        // including) the triggering player
        for (int i = 0; i < startIndex; i++) {
            finalRoundOrder.add(Player.getPlayers().get(i));
        }

        // Now that the order is set, give each player one final turn in this order
        // Note the player who triggered the final round will start first
        for (Player p : finalRoundOrder) {
            System.out.println("\n-------------------------------");
            System.out.println("    " + p.getPlayerName() + "'s final turn!    ");
            System.out.println("-------------------------------\n");

            // Show the current state of the parade before the player makes their move
            System.out.println("\nParade:\n");
            Card.printCards(parade, false, true, true); // show parade cards with line numbers

            // Let the player take their final turn (game logic with lastRound = true)
            gameLogic(p, true);

            // Mark that this was the player’s final move — in case any cleanup is needed
            p.lastRound(p);
        }

        // Now that everyone has finished their final turn, calculate and display the
        // final scores
        score.calculateScore();

        showGameOver();
    }

    private static void showGameOver() {
        try {
            // Generate Figlet-style ASCII art for "GAME OVER"
            String gameOverArt = FigletFont.convertOneLine("GAME OVER");
            // Wrap the ASCII art in red ANSI color codes
            System.out.println("\033[91m" + gameOverArt + "\033[0m");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }
    }

    public static void displayRoundHeader(int currentRound) {
        try {
            // Generate Figlet-style ASCII art for "ROUND X" where X is the current round
            String roundHeaderArt = FigletFont.convertOneLine("ROUND " + currentRound);
            // Wrap the ASCII art in purple ANSI color codes
            System.out.println("\033[95m" + roundHeaderArt + "\033[0m"); // Purple color
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error generating ASCII art.");
        }
    }

    public static void resetGame() {
        // Reset all static variables
        currentRound = 1;
        deck.clear();
        parade.clear();
        score = new Score();
        lastRoundTriggered = false;

        for (Player p : Player.getPlayers()) {
            p.getOpenDeck().clear();
            p.getClosedDeck().clear();
        }

        // clear all players from last game
        Player.getPlayers().clear();
        // clear previous usernames
        UsernameValidator.clearAllUsernames();

    }

}
