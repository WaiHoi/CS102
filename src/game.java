import java.util.*;
import java.io.*;

public class game {
    public ArrayList<Card> deck = new ArrayList<>();
    public ArrayList<Player> players = new ArrayList<>();

    public game(int numPlayers, int numBots) {
        // Add players and bots
        for (int i = 0; i < numPlayers; i++) {
            Player p = new Player();
            players.add(p); 
        }

        for (int i = 0; i < numBots; i++) {
            Player b = new Player();
            players.add(b); 
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
                deck.add(deckUnshuffled.get(r));
                deckUnshuffled.remove(r);
            }
        
        // Deal each player and bot 5 cards 
            for (int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                for (int j = 0; j < 5; j++) {
                    Card c = deck.get(0);
                    deck.remove(0);
                    p.deck.add(c);
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Invalid File");
        }
    }
}
