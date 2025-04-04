package cardgame.model;
import cardgame.GameMenu;
import cardgame.game.*;
import cardgame.io.input.*;
import cardgame.io.output.*;
import cardgame.network.ClientHandler;

import java.util.*;

public class Human extends Player {
    public Card card;
    private int playerID;

    private GameInput input;
    private GameOutput output;

    public Human(String name, int playerID, GameOutput output, GameInput input) {
        super(name, playerID);
        
        // Manual null checks
        if (output == null) {
            throw new IllegalArgumentException("GameOutput cannot be null");
        }
        if (input == null) {
            throw new IllegalArgumentException("GameInput cannot be null");
        }
        
        this.output = output;
        this.input = input;
    }

    public int placeCard() {

        output.sendPrivate("Your closed deck:");
        output.sendPrivate(Card.printCards(closedDeck, false, true));

        // moved to ConsoleInput and NetworkInput
        int selectNumber = input.readInt("Please choose a card to be added into the parade: ", 1, 5) - 1;

        return selectNumber;
    }

    public void lastRound(Player p) {
        // put 2 cards into the the players open deck by choice
        String[] messages = {
                "Please choose the first card to be added into your open deck.",
                "Please choose the second card to be added into your open deck."
        };

        for (int j = 0; j < 2; j++) {
            output.sendPrivate(messages[j]);
            int selectNumber = p.placeCard();
            Card c = p.closedDeck.get(selectNumber);
            p.closedDeck.remove(c);
            p.openDeck.add(c);

            output.sendPrivate("You have picked" + c.getColour() + " " + c.getValue());
        }
        output.sendPrivate("Your current deck:");
        output.sendPrivate(Card.printCards(p.openDeck, true, false));

        output.sendPrivate("Thank you, your last 2 cards will be discarded now.");
    }
    
}
