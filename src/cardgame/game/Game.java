package cardgame.game;

import java.util.*;
import java.io.*;

import cardgame.GameMenu;
import cardgame.model.*;
import cardgame.network.ClientHandler;
import cardgame.network.ParadeServer;
import cardgame.network.TurnManager;

public class Game {
    public static int round = 1;
    public static int counter = 0;
    public static ArrayList<Card> deck = new ArrayList<>();
    public static ArrayList<Card> parade = new ArrayList<>();
    public static Card card;

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

    public static void lastRound(Player p) {
        int selectNumber = 0;
        selectNumber = p.placeCard();

        Card c = p.closedDeck.get(selectNumber);
        p.closedDeck.remove(c);
        parade.add(c);

        if (p instanceof Human) {
            System.out.println(Card.printCards(p.closedDeck, false, false));
        }
    }

    public static void gameLogic(Player p) {
        Scanner sc = new Scanner(System.in);
        int selectNumber = 0;
        selectNumber = p.placeCard();

        Card c = p.closedDeck.get(selectNumber);
        p.closedDeck.remove(c);
        parade.add(c);

        // add new card for player
        Card newCard = deck.get(selectNumber);
        p.closedDeck.add(c);

        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + "\nOpening up your card now...");
        ClientHandler.gameOutput(
                ClientHandler.TAG_PRIVATE + "You have drawn the card: " + c.getColour() + " " + c.getValue() + "\n");

        // Print current parade
        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + "Parade:\n" + Card.printCards(parade, false, false));

        ArrayList<Card> cardsDrawn = new ArrayList<>();
        // Check collectible cards
        for (int i = parade.size() - c.number - 2; i >= 0; i--) {
            if (i < 0)
                break;
            Card currentCard = parade.get(i);

            if (currentCard.getColour().equals(c.getColour()) || currentCard.number < c.number) {
                parade.remove(currentCard);
                p.openDeck.add(currentCard);

                cardsDrawn.add(currentCard);
            }
        }

        // Show cards drawn in the current round.
        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + "\nCards that you collected this round:");
        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + Card.printCards(cardsDrawn, false, false));

        // Show open deck
        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + "\nYour deck of cards:");
        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + Card.printCards(p.openDeck, true, false) + "\n");
        // sc = new Scanner(System.in);
        ClientHandler.gameOutputRaw(ClientHandler.TAG_PRIVATE + "Press Enter to continue > ");
        sc.nextLine();
        ClientHandler.gameOutput("");

    }

    public static void mainFunction() {

        while (true) {
            // round header
            ClientHandler.gameOutput(ClientHandler.TAG_PUBLIC + "\n===== Round " + round + " =====\n");
            ClientHandler.gameOutputRaw(ClientHandler.TAG_PUBLIC + 
                "Parade:\n" + Card.printCards(parade, false, false) + "\n");

            for (Player p : Player.players) {
                TurnManager.setCurrentPlayer(p.getPlayerID());

                // notify turn
                String turnMsg = "\nPlayer " + p.getPlayerName() + "'s turn!\n";
                ClientHandler.gameOutput(ClientHandler.TAG_PUBLIC + turnMsg);

                // calls the move of the player
                gameLogic(p);

                // win conditions
                if (checkPlayersHandForCardFromEachColour(p)) {

                    String winMsg = p.name + " has cards of each colour. The last round will be initiated now.";
                    lastRound(p);
                    ClientHandler.gameOutput(ClientHandler.TAG_PUBLIC + winMsg);

                } else if (deck.isEmpty()) {
                    String deckEmptyMsg = "Deck is empty. The last round will be initiated now.\n";
                    ClientHandler.gameOutput(ClientHandler.TAG_PUBLIC + deckEmptyMsg);
                    lastRound(p);
                }
            }
            round++;
        }
    }
}
