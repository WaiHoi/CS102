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
        initializePlayers(usernames, numHumans); //line 34, adds all Humans to players
        initializeBots(numBots, botDifficulty); //line 53, adds Bots to Players
        initializeDeck(); //line 62, reads deck from the file, populate and shuffles it
        dealCardsToPlayers(); //line 98, each player gets 5 cards, followed by the next player
        initializeParade(); //line 110, the next 6 cards after the all players gets their cards, will be the cards for the parade
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
            while (scanner.hasNext()) {// Read each line of the file
                String[] attributes = scanner.nextLine().split(","); // Split the line by comma into attributes
                Card card = new Card(Integer.parseInt(attributes[0]), attributes[1]);  //index will always only be 0 or 1 because each line in deck.txt only has number first then color
                // Create a Card object using the parsed values
                // First value is an integer, second is a string
                deckUnshuffled.add(card); //add the cards
            }
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: Deck file not found. Please ensure 'deck.txt' exists.");
            System.exit(1); // Exit the program if the file is not found
        }

        // Shuffle the cards
        shuffleDeck(deckUnshuffled);//line 86
    }

    /**
     * Shuffles the unshuffled deck and populates the game's main deck.
     *
     * @param deckUnshuffled The unshuffled deck read from the file.
     */
    private static void shuffleDeck(ArrayList<Card> deckUnshuffled) {
        Random random = new Random(); // Creates a new random number generator 
        while (!deckUnshuffled.isEmpty()) {
            //while unshuffiled deck still has cards
            int randomIndex = random.nextInt(deckUnshuffled.size()); //This picks a random number between 0 and the size of the deck - 1.
            Game.deck.add(deckUnshuffled.remove(randomIndex)); // Remove card from unshuffled list and add to final deck
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