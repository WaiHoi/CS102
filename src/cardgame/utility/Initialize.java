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
        initializePlayers(usernames, numHumans); //line 34
        initializeBots(numBots, botDifficulty); //
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

    //This method creates and adds numHumans Human players (subclass of Player) to the shared list Player.players,
    //using the names from the usernames list and assigning each a unique ID.
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

    // Creates and adds numBots Bot players (subclass of Player) to Player.players,
    // each with a name like "Bot 1" and the specified difficulty level.
    private static void initializeBots(int numBots, BotDifficulty botDifficulty) {
        for (int i = 0; i < numBots; i++) {
            Player.players.add(new Bot("Bot " + (i + 1), botDifficulty)); //
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