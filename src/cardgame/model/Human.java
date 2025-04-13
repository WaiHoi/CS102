package cardgame.model;

import java.util.*;

import cardgame.game.*;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

public class Human extends Player {

    public Human(String name, int playerID) {
        super(name, playerID);
    }

    public int placeCard() {

        // Show the player’s open scoring deck before they choose
        System.out.println(ansi().fgBrightCyan().a("\nYour scoring deck:\n").reset());
        Card.printCards(openDeck, true, true, true); // show total collected cards

        System.out.println(ansi().fgBrightCyan().a("\nYour closed deck:\n").reset());
        Card.printCards(closedDeck, false, true, false);
        System.out.printf("\nPlease choose a card to be added into the parade: ");

        int selectNumber = 0;

        Scanner sc = new Scanner(System.in);

        // prompt for valid entries only
        while (true) {
            try {
                selectNumber = sc.nextInt() - 1;
                sc.nextLine();

                // print if entries are invalid
                if (selectNumber >= 0 && selectNumber <= 4) {
                    break; // valid input, exit loop
                } else {
                    System.out.println("Invalid Entry!");
                    System.out.print("Please enter only numbers from 1 to 5:");
                }

                // prompt again if entries are invalid with the catch of exception
            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Invalid Entry!");
                System.out.print("Please enter only numbers from 1 to 5:");
            }
        }
        return selectNumber;
    }

    public void lastRound(Player p) {
        // put 2 cards into the the players open deck by choice
        String[] messages = {
                "Please choose the first card to be added into your open deck.",
                "Please choose the second card to be added into your open deck."
        };

        // for 2 rounds
        for (int j = 0; j < 2; j++) {
            System.out.println(ansi().fgBrightCyan().a("\nYour closed deck:\n").reset());

            // print out the cards to be chosen to be added to deck
            Card.printCards(closedDeck, false, true, false);

            System.out.print(messages[j] + "\n> ");

            // get the number of card to be added from closed deck into the open deck
            int listSize = 3 - j;
            int selectNumber = p.placeCardLastRound(listSize);
            Card c = p.getClosedDeck().get(selectNumber);

            // remove from closed deck, add to open deck
            p.getClosedDeck().remove(c);
            p.getOpenDeck().add(c);

            System.out.println("You have picked " + c.getColour() + " " + c.getValue() + "\n");

        }

        // display current deck of cards
        System.out.println("Your current deck:");
        Card.printCards(p.getOpenDeck(), true, true, true);
        System.out.println("\nThank you, your last 2 cards will be discarded now.");
    }

    public int placeCardLastRound(int listSize) {

        int selectNumber = 0;

        Scanner sc = new Scanner(System.in);

        // prompt for card number
        while (true) {
            try {
                selectNumber = sc.nextInt() - 1;
                sc.nextLine();

                // if invalid entry, catch exception
                if (selectNumber >= 0 && selectNumber <= listSize) {
                    break; // valid input, exit loop
                } else {
                    System.out.println("Invalid Entry!");
                    System.out.printf("Please enter only numbers from 1 to %d: ", listSize + 1);
                }

            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Invalid Entry!");
                System.out.printf("Please enter only numbers from 1 to %d: ", listSize + 1);
            }
        }
        return selectNumber;
    }
}
