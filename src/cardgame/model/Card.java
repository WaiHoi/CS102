package cardgame.model;

import java.util.*;
import cardgame.utility.AnsiColors;

public class Card {
    private int number;
    private String colour;

    public Card(int n, String c) {
        this.number = n;
        this.colour = c;
    }

    public String getColour() {
        return colour;
    }

    public int getValue() {
        return number;
    }

    private List<String> getAsciiArt(String color) {
        String colorCode = getColorCode(); // Get the ANSI color code for this card's color
        switch (color.toLowerCase()) {
            case "blue": // Princess
                return Arrays.asList(
                    colorCode + "|   .---.   |" + AnsiColors.RESET,  // Crown (aligned to 11 chars)
                    colorCode + "|  ( ^_^ )  |" + AnsiColors.RESET,  // Smiling face
                    colorCode + "|   / A \\   |" + AnsiColors.RESET);   // Dress with an "A-line" shape
    
            case "red": // Clown
                return Arrays.asList(
                    colorCode + "|   .---.   |" + AnsiColors.RESET,  // Hat
                    colorCode + "|  ( @.@ )  |" + AnsiColors.RESET,  // Face with a red nose
                    colorCode + "|   \\_v_/   |" + AnsiColors.RESET); // Smile
    
            case "green": // Dwarf
                return Arrays.asList(
                    colorCode + "|  .-==-.   |" + AnsiColors.RESET,  // Helmet
                    colorCode + "|  (◕ᴥ◕)    |" + AnsiColors.RESET,    // Face with beard
                    colorCode + "|  /_\\_/\\   |" + AnsiColors.RESET);    // Beard and body
    
            case "orange": // Duck
                return Arrays.asList(
                    colorCode + "|   __      |" + AnsiColors.RESET,  // Duck head outline
                    colorCode + "| <(o )___  |" + AnsiColors.RESET,  // Duck face with beak
                    colorCode + "|   ^^ ^^   |" + AnsiColors.RESET);   // Duck body or feet
    
            case "grey": // Bunny
                return Arrays.asList(
                    colorCode + "|   (\\_/)   |" + AnsiColors.RESET,  // Bunny ears and head
                    colorCode + "|  (='.'=)  |" + AnsiColors.RESET,  // Bunny face with nose
                    colorCode + "|  (\")_(\")  |" + AnsiColors.RESET);   // Bunny body with paws
    
            case "purple": // Cat
                return Arrays.asList(
                    colorCode + "|   /\\_/\\   |" + AnsiColors.RESET,
                    colorCode + "|  ( o.o )  |" + AnsiColors.RESET,
                    colorCode + "|   > ^ <   |" + AnsiColors.RESET);
    
            default: // Default empty art
                return Arrays.asList(
                    colorCode + "|           |" + AnsiColors.RESET,
                    colorCode + "|           |" + AnsiColors.RESET,
                    colorCode + "|           |" + AnsiColors.RESET);
        }
    }
    

    /**
     * Generates ASCII art for the card with colored borders and text.
     */
    private List<String> renderCardAscii() {
        String colorCode = getColorCode();// colorCode is based on the card's colour, since renderCardAscii() is called on a Card object

        List<String> lines = new ArrayList<>();

        // Define the borders and other card elements
        String topBottomBorder = colorCode + "+-----------+" + AnsiColors.RESET; // Border width unchanged
        String topNumberLine = colorCode + String.format("| %-9s |", number) + AnsiColors.RESET; // Top number
                                                                                                 // (left-aligned)
        String emptyLine = colorCode + "|           |" + AnsiColors.RESET;

        // Get the ASCII art for this card's color
        List<String> asciiArt = getAsciiArt(colour);

        String bottomNumberLine = colorCode + String.format("| %9s |", number) + AnsiColors.RESET; // Bottom number
                                                                                                   // (right-aligned)

        // Add lines to represent the card
        lines.add(topBottomBorder);
        lines.add(topNumberLine);
        lines.add(emptyLine); //padding
        lines.addAll(asciiArt); // Middle lines (symbol/art)
        lines.add(emptyLine); //padding
        lines.add(bottomNumberLine);
        lines.add(topBottomBorder);

        return lines; // Return the full card as a list of lines (ready to print)
    }

    /**
     * Returns the ANSI color code based on the card's colour.
     */
    private String getColorCode() {
        switch (colour.toLowerCase()) {
            case "red":
                return AnsiColors.BRIGHT_RED;
            case "blue":
                return AnsiColors.BRIGHT_BLUE;
            case "purple":
                return AnsiColors.BRIGHT_MAGENTA;
            case "green":
                return AnsiColors.BRIGHT_GREEN;
            case "orange":
                return AnsiColors.BRIGHT_YELLOW;
            default:
                return AnsiColors.BRIGHT_WHITE;
        }
    }

    /**
    * Prints a list of cards either as ASCII art or simple text.
    * 
    * @param deck               The list of cards to print
    * @param sorting            If true when invoked, sorts cards by colour and value before printing
    * @param displayCardOptions If true, prints full ASCII card art; if false, prints summary string
    * @param lineNumber         If true, shows "Line X:" labels (used for parade); 
    *                           if false, shows numbered labels above cards (used for player hands)
    */
    public static String printCards(ArrayList<Card> deck, boolean sorting, boolean displayCardOptions, 
                                        boolean lineNumber) {
        if (deck.isEmpty()) {
            System.out.println("No card collected this round");
            return "";
        }

        if (sorting) { // If sorting == true, sort cards by colour (A–Z), then by value (low to high)
            deck.sort((card1, card2) -> {
        
                // Compare colours alphabetically (based on Unicode of first different letter)
                int colorComparison = card1.getColour().compareTo(card2.getColour());
        
                // If colours are different, return the result of colorComparison
                // If colours are the same (colorComparison == 0), compare the card values instead
                return (colorComparison != 0) ? colorComparison 
                       : Integer.compare(card1.getValue(), card2.getValue());
            });
        }

        if (!displayCardOptions) { // If displayCardOptions == false, return cards as simple text summary
            StringBuilder printedCards = new StringBuilder("[");
            for (int i = 0; i < deck.size(); i++) {
                printedCards.append(deck.get(i).number).append(" of ").append(deck.get(i).colour);
                if (i < deck.size() - 1)
                    printedCards.append("; "); // Add a semicolon between cards
            }
            printedCards.append("]");
            return printedCards.toString(); //return a formatted summary string, somthing like [2 of Blue; 5 of Green; 9 of Red]
        }

        final int CONSOLE_WIDTH = 120;  // Total width of the console display

        //This creates a visual representation of each card as ASCII art — like rows of strings. 
        //visual representation below:
        /*List<String> red1 = ["╔═══════╗", "║ Red   ║", "║   1   ║", "╚═══════╝"];
        List<String> blue3 = ["╔═══════╗", "║ Blue  ║", "║   3   ║", "╚═══════╝"];
        List<String> green7 = ["╔═══════╗", "║Green  ║", "║   7   ║", "╚═══════╝"];*/
        List<List<String>> allCardsLines = new ArrayList<>(); // Holds the ASCII art lines for each card
        for (Card card : deck) {
            allCardsLines.add(card.renderCardAscii()); // Generate ASCII lines for each card
        }

        // Calculate card width 
        int cardWidth = 13; // Width (in characters) of a single ASCII card
        // Determine how many cards can fit per line
        int cardsPerLine = Math.max(1, CONSOLE_WIDTH / cardWidth); 

        // Print cards in groups that fit within the console width
        for (int group = 0; group < allCardsLines.size(); group += cardsPerLine) {
            int groupEnd = Math.min(group + cardsPerLine, allCardsLines.size());
            
            if (!lineNumber) {
                // If lineNumber == false → we're printing a player's hand (closedDeck)
                // Show numbered labels like [1] [2] above each card
                StringBuilder numberLine = new StringBuilder();
                for (int i = group; i < groupEnd; i++) {
                    int label = i - group + 1;

                    // Align number with card
                    int labelWidth = 3;
                    int padding = (cardWidth - labelWidth) / 2;
                    numberLine.append(" ".repeat(padding))
                              .append("[").append(label).append("]")
                              .append(" ".repeat(padding));

                    // Add space between cards
                    numberLine.append("  "); // spacing between cards
                }
                System.out.println(numberLine.toString());

            } else {
                // If lineNumber == true → we're printing the parade
                // Add a label like "Line 1:", "Line 2:", etc.
                System.out.println("Line " + (group / cardsPerLine + 1) + ":");

            }

            // Print the 9 lines of ASCII art for each card, side by side
            for (int line = 0; line < 9; line++) {
                StringBuilder combinedLine = new StringBuilder();

                for (int i = group; i < groupEnd; i++) {
                    if (line < allCardsLines.get(i).size()) {
                        String cardLine = allCardsLines.get(i).get(line);
                        combinedLine.append(cardLine).append("  "); // Add space between cards
                    }
                }
                System.out.println(combinedLine.toString()); // Print full row of card lines
            }
        }
        return ""; // No string result needed in full ASCII mode
    }
}