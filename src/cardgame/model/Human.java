package cardgame.model;

import cardgame.GameMenu;
import cardgame.game.*;

import java.util.*;

public class Human extends Player {
    public Card card;
    private int playerID;

    public Human(String name) {
        super(name);
    }

    public Human(String name, int playerID) {
        super(name);
        this.playerID = playerID;
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

    public static void initializePlayers() {
        for (int i = 0; i < GameMenu.numHumans; i++) {
            Player p = new Human(GameMenu.usernames.get(i));
            Player.players.add(p);
        }
    }

    public void lastRound(Player p) {
        // put 2 cards into the the players open deck by choice
        String[] messages = {
                "Please choose the first card to be added into your open deck.",
                "Please choose the second card to be added into your open deck."
        };

        for (int j = 0; j < 2; j++) {
            System.out.println(messages[j]);
            int selectNumber = p.placeCard();
            Card c = p.closedDeck.get(selectNumber);
            p.closedDeck.remove(c);
            p.openDeck.add(c);

            System.out.println("You have picked" + c.getColour() + " " + c.getValue());
        }
        System.out.println("Your current deck:");
        System.out.println(Card.printCards(p.openDeck, true, false));

        System.out.println("Thank you, your last 2 cards will be discarded now.");
    }

}
