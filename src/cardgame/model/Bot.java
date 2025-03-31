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
}
