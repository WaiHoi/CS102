package cardgame.utility;

import java.io.*;
import java.util.*;

import cardgame.game.*;
import cardgame.model.*;

public class Initialize {

    /**
     * Initializes the game variables, including players, bots, deck, and parade.
     *
     * @param usernames     List of usernames for human players.
     * @param numHumans     Number of human players.
     * @param numBots       Number of bot players.
     * @param botDifficulty Difficulty level for bots.
     */
    public static void initializeVariables(List<String> usernames, int numHumans, 
                                           int numBots, BotDifficulty botDifficulty) {
        initializePlayers(usernames, numHumans);
        initializeBots(numBots, botDifficulty);
        initializeDeck();
        dealCardsToPlayers();
        initializeParade();
    }

    /**
     * Initializes human players and adds them to the player list.
     *
     * @param usernames List of usernames for human players.
     * @param numHumans Number of human players.
     */
    private static void initializePlayers(List<String> usernames, int numHumans) {
        for (int i = 0; i < numHumans; i++) {
            int playerID = i + 1;
            Player.players.add(new Human(usernames.get(i), playerID));
        }
    }

    /**
     * Initializes bot players and adds them to the player list.
     *
     * @param numBots       Number of bot players.
     * @param botDifficulty Difficulty level for bots.
     */
    private static void initializeBots(int numBots, BotDifficulty botDifficulty) {
        for (int i = 0; i < numBots; i++) {
            Player.players.add(new Bot("Bot " + (i + 1), botDifficulty));
        }
    }

    /**
     * Reads the deck from a file ("deck.txt"), shuffles it, and populates the game deck.
     */
    private static void initializeDeck() {
        ArrayList<Card> deckUnshuffled = new ArrayList<>();
        try (Scanner scanner = new Scanner(new File("deck.txt"))) {
            while (scanner.hasNext()) {
                String[] attributes = scanner.nextLine().split(",");
                Card card = new Card(Integer.parseInt(attributes[0]), attributes[1]);
                deckUnshuffled.add(card);
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Deck file not found. Please ensure 'deck.txt' exists.");
            System.exit(1); // Exit the program if the file is not found
        }

        // Shuffle the cards
        shuffleDeck(deckUnshuffled);
    }

    /**
     * Shuffles the unshuffled deck and populates the game's main deck.
     *
     * @param deckUnshuffled The unshuffled deck read from the file.
     */
    private static void shuffleDeck(ArrayList<Card> deckUnshuffled) {
        Random random = new Random();
        while (!deckUnshuffled.isEmpty()) {
            int randomIndex = random.nextInt(deckUnshuffled.size());
            Game.deck.add(deckUnshuffled.remove(randomIndex));
        }
    }

    /**
     * Deals 5 cards to each player from the game deck.
     */
    private static void dealCardsToPlayers() {
        for (Player player : Player.players) {
            for (int i = 0; i < 5; i++) {
                Card card = Game.deck.remove(0); // Remove card from top of the deck
                player.closedDeck.add(card); // Add card to player's closed deck
            }
        }
    }

    /**
     * Draws 6 cards from the game deck into the parade.
     */
    private static void initializeParade() {
        for (int i = 0; i < 6; i++) {
            Card card = Game.deck.remove(0); // Remove card from top of the deck
            Game.parade.add(card); // Add card to parade
        }
    }
}



// package cardgame.utility;
// import java.io.*;
// import java.util.*;

// import cardgame.game.*;
// import cardgame.model.*;

// public class Initialize {
//     public static void initializeVariables(List<String> usernames, int numHumans, 
//                                             int numBots, BotDifficulty botDifficulty) {

//         // initialise players and bots 
//         for (int i = 0; i < numHumans; i++) {
//             int playerID = i + 1;
//             Player.players.add(new Human(usernames.get(i), playerID));
//         }

//         for (int i = 0; i < numBots; i++) {
//             Player.players.add(new Bot("Bot " + (i + 1), botDifficulty));
//         }

//         // import the cards from deck.txt
//         Scanner sc = null;
//         try {
//             ArrayList<Card> deckUnshuffled = new ArrayList<>();
//             sc = new Scanner(new File("deck.txt"));
//             while (sc.hasNext()) {
//                 String[] attributes = sc.nextLine().split(",");
//                 Card c = new Card(Integer.parseInt(attributes[0]), attributes[1]);
//                 deckUnshuffled.add(c);
//             }
//             sc.close();

//         // shuffle the cards in deck
//             Random rand = new Random();
//             while (deckUnshuffled.size() > 0) {
//                 int r = rand.nextInt(deckUnshuffled.size());
//                 Game.deck.add(deckUnshuffled.get(r));
//                 deckUnshuffled.remove(r);
//             }
        
//         // Deal each player and bot 5 cards 
//             for (int i = 0; i < Player.players.size(); i++) {
//                 Player p = Player.players.get(i);
//                 for (int j = 0; j < 5; j++) {
//                     Card c = Game.deck.get(0);
//                     Game.deck.remove(0);
//                     p.closedDeck.add(c);
//                 }
//             }
        
//         // Draw 6 cards out from the deck into parade
//         for (int i = 0; i < 6; i++) {
//             Card c = Game.deck.get(0);
//             Game.deck.remove(0);
//             Game.parade.add(c);
//         }

//         } catch (FileNotFoundException e) {
//             System.out.println("Invalid File");
//         }

//     }

// }