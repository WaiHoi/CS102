package cardgame.utility;

import java.io.*;
import java.util.*;

import cardgame.game.*;
import cardgame.io.input.*;
import cardgame.io.output.*;
import cardgame.model.*;
import cardgame.network.ClientHandler;
import cardgame.*;

import org.fusesource.jansi.AnsiConsole;
import org.fusesource.jansi.Ansi;
import static org.fusesource.jansi.Ansi.ansi;

public class Initialize {

    private static GameOutput output;
    private static GameInput input;

    public static void initializeVariables() {

        // initialise IO
        if (ClientHandler.isNetworkMode()) {
            output = ClientHandler.getCurrentClientOutput();
            input = ClientHandler.getCurrentClientInput();
        } else {
            output = new ConsoleOutput();
            input = new ConsoleInput();
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
            output.sendError("Invalid File");
        }

    }

}