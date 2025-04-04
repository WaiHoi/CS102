package cardgame.model;

import cardgame.GameMenu;
import cardgame.game.*;
import cardgame.io.output.*;

import java.util.Random;

public class Bot extends Player {
    private GameOutput output;

    public Bot(String name, GameOutput output) {
        super(name, -1); // -1 indicates bot
        
        // Manual null check
        if (output == null) {
            throw new IllegalArgumentException("GameOutput cannot be null");
        }
        
        this.output = output;
    }

    public int placeCard() {
        Random rand = new Random();
        int selectNumber = rand.nextInt(closedDeck.size());
        return selectNumber;
    }

    // public static void initializePlayers() {
    //     for (int i = 0; i < GameMenu.numBots; i++) {
    //         Player b = new Bot(GameMenu.usernames.get(i + GameMenu.numHumans));
    //         Player.players.add(b);
    //     }
    // }

    public void lastRound(Player p) {
        // put 2 cards into the the bots open deck by randomization
        output.broadcastToAll("Picking 2 cards by random...");

        for (int j = 0; j < 2; j++) {
            int selectNumber = p.placeCard();
            Card c = p.closedDeck.get(selectNumber);
            p.openDeck.add(c);

            output.broadcastToAll("Randomly picked " + c.getColour() + " " + c.getValue());

        }

        output.broadcastToAll("Your current deck:");
        output.broadcastToAll(Card.printCards(p.openDeck, true, false));
    }
}
