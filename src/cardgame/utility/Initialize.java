package cardgame.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class Initialize {

    public static int numPlayers;
    public static int numBots;

    public static void displayMenu() {

        numPlayers = 0;
        numBots = 0;
        System.out.println("Welcome to a game of Parades!\nEnjoy and have fun!");

        while (numPlayers + numBots < 2 || numPlayers + numBots > 6) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter number of human players: ");
                numPlayers = sc.nextInt();
                sc.nextLine();

                if (numPlayers < 0 || numPlayers > 6) {
                    throw new InputMismatchException();
                }

                System.out.print("Enter number of bot players: ");
                numBots = sc.nextInt();
                sc.nextLine();

                if (numBots < 0 || numBots > 6) {
                    throw new InputMismatchException();
                }

                if (numPlayers + numBots < 2 || numPlayers + numBots > 6) {
                    System.out.println("Total number of players and bots should be between 2 and 6!\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be a positive integer and not more than 6!\n");
            }

        }
    }

    public static void initializeVariables() {
        for (int i = 0; i < numPlayers; i++) {
            Player p = new Human("name");
            Player.players.add(p); 
        }

        for (int i = 0; i < numBots; i++) {
            Player b = new Bot("name");
            Player.players.add(b); 
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