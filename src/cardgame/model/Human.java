package cardgame.model;
import cardgame.game.*;
import java.util.InputMismatchException;
import java.util.*;

public class Human extends Player {
    public Card card;

    public Human(String name) {
        super(name);
    }

    public int placeCard() {
        System.out.println("Your closed deck:");
        System.out.println(Card.printCards(closedDeck, false, true));
        System.out.print("Please choose a card to be added into the parade: ");

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
}
