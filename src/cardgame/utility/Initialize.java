package cardgame.utility;

import java.io.*;
import java.util.*;

import cardgame.game.*;
import cardgame.model.*;

public class Initialize {
    public static void initializeVariables(List<String> usernames, int numHumans, 
                                            int numBots, BotDifficulty botDifficulty) {

        // initialise players and bots 
        for (int i = 0; i < numHumans; i++) {
            int playerID = i + 1;
            Player.players.add(new Human(usernames.get(i), playerID));
        }

        for (int i = 0; i < numBots; i++) {
            Player.players.add(new Bot("Bot " + (i + 1), botDifficulty));
        }

        // import the cards from deck.txt
        Scanner sc = null;
        try {
            ArrayList<Card> deckUnshuffled = new ArrayList<>();
            sc = new Scanner(new File("deck.txt"));
            while (sc.hasNext()) {
                String[] attributes = sc.nextLine().split(",");
                Card c = new Card(Integer.parseInt(attributes[0]), attributes[1]);
                deckUnshuffled.add(c);
            }
            sc.close();

        // shuffle the cards in deck
            Random rand = new Random();
            while (deckUnshuffled.size() > 0) {
                int r = rand.nextInt(deckUnshuffled.size());
                Game.deck.add(deckUnshuffled.get(r));
                deckUnshuffled.remove(r);
            }
        
        // Deal each player and bot 5 cards 
            for (int i = 0; i < Player.players.size(); i++) {
                Player p = Player.players.get(i);
                for (int j = 0; j < 5; j++) {
                    Card c = Game.deck.get(0);
                    Game.deck.remove(0);
                    p.closedDeck.add(c);
                }
            }
        
        // Draw 6 cards out from the deck into parade
        for (int i = 0; i < 6; i++) {
            Card c = Game.deck.get(0);
            Game.deck.remove(0);
            Game.parade.add(c);
        }

        } catch (FileNotFoundException e) {
            System.out.println("Invalid File");
        }

    }

}