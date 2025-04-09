package cardgame.model;

import java.util.*;

import cardgame.game.*;
import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

public class Human extends Player {
    public Card card;
    private int playerID;

    public Human(String name, int playerID) {
        super(name, playerID);
    }

    public int placeCard() {

        System.out.println(ansi().fgBrightCyan().a("Your closed deck:").reset());
        System.out.println(Card.printCards(closedDeck, false, true));
        System.out.printf("Please choose a card to be added into the parade: ");

        int selectNumber = 0;

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                selectNumber = sc.nextInt() - 1;
                sc.nextLine();

                if (selectNumber >= 0 && selectNumber <= 4) {
                    break; // valid input, exit loop
                } else {
                    System.out.println("Invalid Entry!");
                    System.out.print("Please enter only numbers from 1 to 5:");
                }

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

        for (int j = 0; j < 2; j++) {
            System.out.print(messages[j] + "\n> ");

            int listSize = 3 - j;
            int selectNumber = p.placeCardLastRound(listSize);
            Card c = p.closedDeck.get(selectNumber);
            p.closedDeck.remove(c);
            p.openDeck.add(c);

            System.out.println("You have picked " + c.getColour() + " " + c.getValue() + "\n");

        }
        System.out.println("Your current deck:");
        System.out.println(Card.printCards(p.openDeck, true, false));
        System.out.println("\nThank you, your last 2 cards will be discarded now.");
    }

    public int placeCardLastRound(int listSize) {

        int selectNumber = 0;

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                selectNumber = sc.nextInt() - 1;
                sc.nextLine();

                if (selectNumber >= 0 && selectNumber <= listSize) {
                    break; // valid input, exit loop
                } else {
                    System.out.println("Invalid Entry!");
                    System.out.printf("Please enter only numbers from 1 to %d: ", listSize);
                }

            } catch (InputMismatchException e) {
                sc.nextLine();
                System.out.println("Invalid Entry!");
                System.out.printf("Please enter only numbers from 1 to %d: ", listSize);
            }
        }
        return selectNumber;
    }   
    
}
