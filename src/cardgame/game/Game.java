package cardgame.game;

import java.util.*;
import java.io.*;

import cardgame.GameMenu;
import cardgame.model.*;

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
        // Collect colors present in openDeck
        ArrayList<String> foundColors = new ArrayList<>();

        for (Card card : p.openDeck) {
            if (!foundColors.contains(card.colour)) { // Avoid duplicates
                foundColors.add(card.colour);
            }
        }

        // Return true if all required colors are found
        return foundColors.containsAll(requiredColors);
    }

    public static void logicalFunction(Player p) {
        Scanner sc = new Scanner(System.in);
        int selectNumber = 0;
        p.placeCard();

        Card c = p.closedDeck.get(selectNumber);
        p.closedDeck.remove(c);
        parade.add(c);

        // add new card for player
        Card newCard = deck.get(0);
        deck.remove(0);
        p.closedDeck.add(newCard);
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

            if (currentCard.getColour().equals(c.getColour()) || currentCard.number < c.number) {
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
        //sc = new Scanner(System.in);
        System.out.print("Press Enter to continue > ");
        sc.nextLine();
        System.out.println();
    }

    public static void mainFunction() {
        System.out.println("\n----- Round " + round + " -----\n");
        System.out.print("Parade:\n" + Card.printCards(parade, false, false) + "\n");

        // iterates through the players
        // n random from 0 to players.size() - 1
        for (int i = 0/*n*/; i < Player.players.size(); i++) {

            // get the first player
            Player p = Player.players.get(i);
            System.out.println(i);

            // calls the move of the player
            System.out.println("\nPlayer " + p.name + "'s turn!\n");
            logicalFunction(p);

            if (checkPlayersHandForCardFromEachColour(p)){
                System.out.println("player" + p.name + " has cards of each colour. game ends");
            } else if(deck.isEmpty()) {
                System.out.println("deck is empty. game ends");

            } else if (Player.players.size() == i + 1) {
                i = -1;
                round++;
                System.out.println("\n----- Round " + round + " -----\n");
            }

            // count++;
            // round = count / player.size();
        }
    }

}
