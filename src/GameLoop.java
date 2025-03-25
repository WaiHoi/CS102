import java.util.*;

public class GameLoop {
    /* 
     * 2. Draw anonymous card
     * 3. Determine safe cards
     * 4. Draw cards that are same color or number into the players deck
     * 5. Check if any players have a card from each color deck OR when deck is
     * empty. END LOOP
     */


    public int round = 1;
    public int counter = 0;
    public ArrayList<Player> players;
    public ArrayList<Card> deck;
    public ArrayList<Card> parade;
    public boolean isBot;
    // public game game;

    public GameLoop(ArrayList<Player> players, ArrayList<Card> deck, ArrayList<Card> parade) {
        this.players = players;
        this.deck = deck;
        this.parade = parade;
    }

    public String printCards(ArrayList<Card> deck, boolean sorting){
        if (deck.isEmpty()) {
            return "[]";
        }
    
        if (sorting){
            // Sort the cards by colour first, then by value
            deck.sort((card1, card2) -> {
                int colorComparison = card1.getColour().compareTo(card2.getColour());
                return (colorComparison != 0) ? colorComparison : Integer.compare(card1.getValue(), card2.getValue());
            });
        }
    
        // Construct the formatted string
        StringBuilder printedCards = new StringBuilder("[");
        for (Card card : deck) {
            printedCards.append(card.colour).append(", ").append(card.number).append("; ");
        }
    
        // Remove the last "; " and close the bracket
        printedCards.setLength(printedCards.length() - 2);
        printedCards.append("]");
    
        return printedCards.toString();
    }

    public boolean checkPlayersHandForCardFromEachColour(Player p) {
        // Define the required colors
        ArrayList<String> requiredColors = new ArrayList<>(Arrays.asList(
                "red", "blue", "green", "grey", "purple", "orange"));
        // Collect colors present in openDeck
        ArrayList<String> foundColors = new ArrayList<>();

        for (Card card : p.openDeck) {
            if (!foundColors.contains(card.colour)) { // Avoid duplicates
                foundColors.add(card.colour);
            }
        }

        // Return true if all required colors are found
        return foundColors.containsAll(requiredColors);
    }

    public void logicalFunction(Player p) {
        Scanner sc = new Scanner(System.in);
        int selectNumber = 0;

        // if player is human
        if (!p.isBot) {
            System.out.print("Enter a number between 1 and 5:");

            while (true) {
                try{
                    selectNumber = sc.nextInt() - 1;
                    sc.nextLine();

                if (selectNumber >= 0 && selectNumber <= 4) {
                    break; // valid input, exit loop
                }else{
                    System.out.println("Invalid Entry!");
                    System.out.print("Please enter only numbers from 1 to 5:");
                }

                }catch (InputMismatchException e){
                    sc.nextLine();
                    System.out.println("Invalid Entry!");
                    System.out.print("Please enter only numbers from 1 to 5:");
                }
            }
        }
        // Randomly pick a card from anonDeck
        else {
            Random rand = new Random();
            selectNumber = rand.nextInt(p.anonDeck.size());
        }
        Card c = p.anonDeck.get(selectNumber);
        p.anonDeck.remove(c);
        parade.add(c);

        // add new card for player
        Card newCard = deck.get(0);
        deck.remove(0);
        p.anonDeck.add(newCard);
        System.out.println("\nOpening up your card now...");
        System.out.println("You have drawn the card: " + c.getColour() + " " + c.getValue() + "\n");

        // Print current parade
        System.out.println("Parade:\n" + printCards(parade, false));

        ArrayList<Card> cardsDrawn = new ArrayList<>();
        // Check collectible cards
        for (int i = parade.size() - c.number - 2; i >= 0; i--) {
            if (i < 0)
                break;
            Card currentCard = parade.get(i);

            if (currentCard.getColour().equals(c.getColour()) || currentCard.number < c.number) {
                parade.remove(currentCard);
                p.openDeck.add(currentCard);

                cardsDrawn.add(currentCard);
            }
        }

        // Show cards drawn in the current round.
        System.out.println("\nCards that you collected this round:");
        System.out.println(printCards(cardsDrawn, false));


        // Show open deck
        System.out.println("\nYour deck of cards:");
        System.out.println(printCards(p.openDeck, true) + "\n");
        //sc = new Scanner(System.in);
        System.out.print("Press Enter to continue > ");
        sc.nextLine();
        System.out.println();
    }

    public void mainFunction() {
        System.out.println("\n----- Round" + round + " -----\n");
        System.out.print("Parade:\n" + printCards(parade, false) + "\n");

        // iterates through the players
        for (int i = 0; i < players.size(); i++) {

            // get the first player
            Player p = players.get(i);

            // calls the move of the player
            System.out.println("\nPlayer " + i + "'s turn!\n");
            logicalFunction(p);

            if (checkPlayersHandForCardFromEachColour(p)){
                System.out.println("player" + i + " has cards of each colour. game ends");
            } else if(deck.isEmpty()) {
                System.out.println("deck is empty. game ends");

            } else if (players.size() == i + 1) {
                i = -1;
                round++;
                System.out.println("Round:" + round + "\n");
            }
        }
    }

}