package cardgame.model;

import cardgame.GameMenu;
import cardgame.game.*;
import java.util.Random;

public class Bot extends Player {
    public Bot(String name) {
        super(name);
    }

    public int placeCard() {
        Random rand = new Random();
        int selectNumber = rand.nextInt(closedDeck.size());
        return selectNumber;
    }

    public static void initializePlayers() {
        for (int i = 0; i < GameMenu.numBots; i++) {
            Player b = new Bot(GameMenu.usernames.get(i + GameMenu.numHumans));
            Player.players.add(b);
        }
    }

    public void lastRound(Player p) {
        // put 2 cards into the the bots open deck by randomization
        System.out.println("Picking 2 cards by random...");

        for (int j = 0; j < 2; j++) {
            int selectNumber = p.placeCard();
            Card c = p.closedDeck.get(selectNumber);
            p.openDeck.add(c);

            System.out.println("Randomly picked " + c.getColour() + " " + c.getValue());

        }

        System.out.println("Your current deck:");
        System.out.println(Card.printCards(p.openDeck, true, false));
    }
}
