package cardgame.game;

import java.util.*;

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

        int selectNumber = p.placeCard();
        Card c = p.closedDeck.get(selectNumber);
        p.closedDeck.remove(c);
        parade.add(c);

        // if last round, do not add new card
        if (!lastRound) {
            addNewCard(p);
        }

        System.out.println("\nOpening up " + p.getPlayerName() + "'s card now...");
        System.out.println(p.getPlayerName() + " has drawn the card: " + c.getColour() + " " + c.getValue() + "\n");

        ArrayList<Card> cardsDrawn = new ArrayList<>();

        // Check collectible cards
        for (int i = parade.size() - c.getValue() - 2; i >= 0; i--) {
            if (i < 0)
                break;
            Card currentCard = parade.get(i);

            if (currentCard.getColour().equals(c.getColour()) || currentCard.getValue() <= c.getValue()) {
                parade.remove(currentCard);
                p.openDeck.add(currentCard);

                cardsDrawn.add(currentCard);
            }
        }

        // Print current parade as ASCII cards
        System.out.println("Updated Parade:\n");
        Card.printCards(parade, false, true, true); // displayCardOptions = true, lineNumber = true

        // Show cards drawn as ASCII cards
        System.out.println("\nCards that " + p.getPlayerName() + " has collected this round:");
        Card.printCards(cardsDrawn, false, true, true); // displayCardOptions = true, lineNumber = true

        // Show open deck as ASCII cards
        System.out.println("\n" + p.getPlayerName() + "'s deck of cards:");
        Card.printCards(p.openDeck, true, true, true); // displayCardOptions = true, lineNumber = true

        // move to next turn
        TurnManager.nextTurn();

        Scanner sc = new Scanner(System.in);
        System.out.print("\nPress Enter to continue> ");
        sc.nextLine();
    }

    public static void mainFunction(boolean isNetworkMode) {

        displayRoundHeader(currentRound);

        while (true) {    
            boolean processLastRound = false;

            // get current player
            int currentPlayerID = TurnManager.getCurrentPlayerID();
            Player currentPlayer = Player.players.get(currentPlayerID - 1);

            // sent to all
            System.out.println("\n--------------------------");
            System.out.println("    " + currentPlayer.name + "'s turn!    ");
            System.out.println("--------------------------\n");

            System.out.println("Parade:\n");
            Card.printCards(parade, false, true, true); // displayCardOptions = true, isParade = true

            // Play the current turn (regular or last round)
            gameLogic(currentPlayer, lastRoundTriggered);

            // Check for last round conditions
            if (!lastRoundTriggered && (checkPlayersHandForCardFromEachColour(currentPlayer) ||
                    deck.isEmpty())) {
                lastRoundTriggered = true;
                System.out.println("Last round triggered by " + currentPlayer.name + "!\n");
                processLastRound = true;
            }

            // If last round was just triggered, break to process final turns
            if (processLastRound) {
                executeLastRound();
                break;
            }

            if (!lastRoundTriggered && TurnManager.getCurrentPlayerID() == 1) {
                currentRound++;
                displayRoundHeader(currentRound);
            } 

        }

        // Game over logic
        System.out.println("+----------------------+");
        System.out.println("|      GAME OVER       |");
        System.out.println("+----------------------+");

        Score score = new Score();

        // calculate scores
        score.calculateScore(Player.players);

        // print scores for each player
        System.out.println("Scores:");
        for (Map.Entry<Player, Integer> entry : score.getPlayerScoreCount().entrySet()) {
            System.out.println(entry.getKey().getPlayerName() + ": " + entry.getValue());
        }

        // determine winner
        Player winner = score.determineWinner(Player.players);
        int winnerScore = score.getPlayerScore(winner);

        System.out.println(winner.getPlayerName() + " has a score of " + winnerScore);


        System.out.println("Total rounds played: " + currentRound);
    }

    private static void executeLastRound() {
        System.out.println("\n+-------------------------+");
        System.out.println("|       FINAL ROUND       |");
        System.out.println("+-------------------------+");

        // get player who triggered last round
        int triggeringPlayerID = TurnManager.getCurrentPlayerID();
        List<Player> finalRoundOrder = new ArrayList<>();

        // add players from triggering player to end
        for (int i = triggeringPlayerID - 1; i < Player.players.size(); i++) {
            finalRoundOrder.add(Player.players.get(i));
        }

        // add players from start to triggering player
        for (int i = 0; i < triggeringPlayerID - 1; i++) {
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
    }

    public static void resetGameState() {
        currentRound = 1;
        deck.clear();
        parade.clear();
        lastRoundTriggered = false;
    }

    public static void displayRoundHeader(int currentRound) {
        System.out.println("\n+----------------------+");
        System.out.printf ("|       ROUND %-2d       |\n", currentRound);
        System.out.println("+----------------------+");
    }

}
