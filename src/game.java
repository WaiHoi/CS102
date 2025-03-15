import java.util.*;
import java.io.*;

public class game {
    ArrayList<card> deck = new ArrayList<>();

    public void initialise() {
        // import the cards from deck.txt
        Scanner sc = null;
        try {
            ArrayList<card> deckUnshuffled = new ArrayList<>();
            sc = new Scanner(new File("deck.txt"));
            while (sc.hasNext()) {
                String[] attributes = sc.nextLine().split(",");
                card c = new card(Integer.parseInt(attributes[0]), attributes[1]);
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

        } catch (FileNotFoundException e) {
            System.out.println("Invalid File");
        }

    }
}
