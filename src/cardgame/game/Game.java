package cardgame.game;

import java.util.*;
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

        // check if its current player's turn
        if (TurnManager.getNetworkMode() && !TurnManager.isMyTurn(p.getPlayerID())) {
            return;
        }

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

        // move to next turn
        TurnManager.nextTurn();
    }

    public static void mainFunction(boolean isNetworkMode) {

        if (isNetworkMode) {
            // For network games, get the output from the first connected client
            if (!ClientHandler.clients.isEmpty()) {
                output = ClientHandler.clients.values().iterator().next().getOutput();
            } else {
                output = null;
            }
        } else {
            // For local games, use console output
            output = new ConsoleOutput();
        }

        output.broadcastToAll("\n----- Round " + currentRound + " -----\n");
        output.broadcastToAll("Parade:\n" + Card.printCards(parade, false, false) + "\n");

        while (true) {
            boolean processLastRound = false;
            Player triggeringPlayer = null;

            // get current player
            int currentPlayerID = TurnManager.getCurrentPlayerID();
            Player currentPlayer = Player.players.get(currentPlayerID - 1);

            // sent to all
            output.broadcastToAll(currentPlayer.name + "'s turn!\n");

            // Play the current turn (regular or last round)
            gameLogic(currentPlayer, lastRoundTriggered);

            // Check for last round conditions
            if (!lastRoundTriggered && (checkPlayersHandForCardFromEachColour(currentPlayer) ||
                    deck.isEmpty())) {
                lastRoundTriggered = true;
                triggeringPlayer = currentPlayer;
                output.broadcastToAll("Last round triggered by " + currentPlayer.name + "!");
                processLastRound = true;
            }

            // If last round was just triggered, break to process final turns
            if (processLastRound) {
                executeLastRound();
                break;
            }

            if (!lastRoundTriggered && TurnManager.getCurrentPlayerID() == 1) {
                currentRound++;
                output.broadcastToAll("\n----- Round " + currentRound + " -----\n");
            }

            // // Process regular turns
            // for (Player p : Player.players) {

            // // sent to all
            // output.broadcastToAll(p.name + "'s turn!\n");

            // // Play the current turn (regular or last round)
            // gameLogic(p, lastRoundTriggered);

            // // Check for last round conditions
            // if (!lastRoundTriggered && (checkPlayersHandForCardFromEachColour(p) ||
            // deck.isEmpty())) {
            // lastRoundTriggered = true;
            // triggeringPlayer = p;
            // output.broadcastToAll("Last round triggered by " + p.name + "!");
            // processLastRound = true;
            // }

            // // If last round was just triggered, break to process final turns
            // if (processLastRound) {
            // break;
            // }
            // }

            // // Process last round if triggered
            // if (processLastRound) {
            // executeLastRound();
            // break; // Exit the game loop after last round
            // }

            // // Only increment round if not in last round
            // if (!lastRoundTriggered) {
            // currentRound++;
            // output.broadcastToAll("\n----- Round " + currentRound + " -----\n");
            // }
        }

        // Game over logic
        output.broadcastToAll("\n=== GAME OVER ===");
        output.broadcastToAll("Total rounds played: " + currentRound);
    }

    private static void executeLastRound() {
        output.broadcastToAll("\n----- FINAL ROUND -----\n");

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
            output.broadcastToAll("\n=====" + p.name + "'s final turn!=====\n");
            
            // set the current player for turn management
            TurnManager.setCurrentPlayer(p.getPlayerID());
            
            gameLogic(p, true);
            p.lastRound(p);
        }

    }
}
