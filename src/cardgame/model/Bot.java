package cardgame.model;

import java.util.*;

import cardgame.game.*;

public class Bot extends Player {
    private final BotDifficulty botDifficulty;

    public Bot(String name, BotDifficulty botDifficulty) {
        super(name, -1); // -1 indicates bot
        this.botDifficulty = botDifficulty;

    }

    public BotDifficulty getDifficulty() {
        return botDifficulty;
    }

    public int placeCard() {
        switch (botDifficulty) {
            case EASY:
                return easyDifficulty();
            case MEDIUM:
                return mediumDifficulty();
            case HARD:
                return hardDifficulty();
            default:
                return easyDifficulty();
        }
    }

    public int easyDifficulty() {
        // chooses a random card 
        Random rand = new Random();
        int selectNumber = rand.nextInt(closedDeck.size());
        return selectNumber;
    }

// Medium difficulty bot strategy: 
// 80% of the time → play card whose colour appears least in the parade
// 20% of the time → act like easy bot (random or lowest value)
public int mediumDifficulty() {

    // 20% chance to play like easyDifficulty()
    Random rand = new Random();
    if (rand.nextInt(100) < 20) {
        return easyDifficulty(); // fallback to easy mode
    }

    int selectNumber = 0; // index of the card to be selected
    int fewestMatchingColours = Integer.MAX_VALUE; // start with very high count

    // Loop through all cards in the bot's closed deck
    for (int i = 0; i < closedDeck.size(); i++) {
        Card card = closedDeck.get(i); // current card
        String colour = card.getColour(); // get card's colour

        // Count how many cards in the parade have the same colour
        int cardCount = 0;
        for (Card paradeCard : Game.parade) {
            if (paradeCard.getColour().equals(colour)) {
                cardCount++; // increase count if colour matches
            }
        }

        // If this colour is less common than previous best, select this card
        if (cardCount < fewestMatchingColours) {
            fewestMatchingColours = cardCount; // update fewest seen
            selectNumber = i; // update best index so far
        }
    }

    // Return the index of the selected card
    return selectNumber;
}


    public int hardDifficulty() {
        // choose best card 
        // simulate outcome of each move
        // pick the one that results in lowest cost

        // 10% chance to pick randomly
        Random rand = new Random();
        if (rand.nextInt(100) < 10) {
            return easyDifficulty();
        }

        // select the card with the lowest penalty 
        int selectNumber = 0;
        // start with highest possible number
        int minScore = Integer.MAX_VALUE;

        for (int i = 0; i < closedDeck.size(); i++) {
            // get bots hand
            Card card = closedDeck.get(i);
            // simulate what would happen if card is played
            int score = scorePerCard(card);
    
            // get the lowest penalty 
            if (score < minScore) {
                minScore = score;
                selectNumber = i;
            }
        }
        return selectNumber;
    }

    private int scorePerCard(Card card) {
        int score = 0;

        for (int i = card.getValue(); i < Game.parade.size(); i++) {
            Card c = Game.parade.get(i);

            // matches the same colour or if value is less than or equal
            if (c.getColour().equals(card.getColour()) || c.getValue() <= card.getValue()) {
                score += c.getValue();
            }
        }
        return score;
    }

    public void lastRound(Player p) {
        // put 2 cards into the the bots open deck by randomization
        System.out.println("Picking 2 cards by random...\n");

        for (int j = 0; j < 2; j++) {
            int listSize = p.closedDeck.size();;
            int selectNumber = p.placeCardLastRound(listSize);
            Card c = p.closedDeck.get(selectNumber);
            p.closedDeck.remove(c);
            p.openDeck.add(c);

            System.out.println("Randomly picked " + c.getColour() + " " + c.getValue());
        }

        System.out.println("\nYour current deck:");
        Card.printCards(p.openDeck, true, true, true);

    }

    public int placeCardLastRound(int listSize) {
        // save a copy of the deck
        ArrayList<Card> originalClosedDeck = new ArrayList<>(this.closedDeck);
        
        try {
            
            // use existing difficulty methods
            switch (botDifficulty) {
                case EASY:
                    return easyDifficulty();
                case MEDIUM:
                    return mediumDifficulty();
                case HARD:
                    return hardDifficulty();
                default:
                    return easyDifficulty();
            }
        } finally {
            // restore the original closed deck
            this.closedDeck = originalClosedDeck;
        }
    }
}
