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
                        colorCode + "|   \\^_^/   |" + AnsiColors.RESET,
                        colorCode + "|  ( 'o' )  |" + AnsiColors.RESET,
                        colorCode + "|   \\_=_/   |" + AnsiColors.RESET);
            case "red": // Clown
                return Arrays.asList(
                        colorCode + "|   *<:o)   |" + AnsiColors.RESET,
                        colorCode + "|  <(:o)>   |" + AnsiColors.RESET,
                        colorCode + "|   <:o*>   |" + AnsiColors.RESET);
            case "green": // Dwarf
                return Arrays.asList(
                        colorCode + "|   o==[]   |" + AnsiColors.RESET,
                        colorCode + "|  ( o_o )  |" + AnsiColors.RESET,
                        colorCode + "|   \\___/   |" + AnsiColors.RESET);
            case "orange": // Duck
                return Arrays.asList(
                        colorCode + "|   ~<')    |" + AnsiColors.RESET,
                        colorCode + "|  ~<')___  |" + AnsiColors.RESET,
                        colorCode + "|     \"     |" + AnsiColors.RESET);
            case "grey": // Bunny
                return Arrays.asList(
                        colorCode + "|   ('>     |" + AnsiColors.RESET,
                        colorCode + "|  /rr *\\)) |" + AnsiColors.RESET,
                        colorCode + "| ((`\\___   |" + AnsiColors.RESET);
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
        String colorCode = getColorCode();
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
        lines.add(emptyLine);
        lines.addAll(asciiArt); // Insert the ASCII art here
        lines.add(emptyLine);
        lines.add(bottomNumberLine);
        lines.add(topBottomBorder);

        return lines;
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

    public static String printCards(ArrayList<Card> deck, boolean sorting, boolean displayCardOptions, 
                                        boolean lineNumber) {
        if (deck.isEmpty()) {
            System.out.println("No card collected this round");
            return "";
        }

        if (sorting) {
            deck.sort((card1, card2) -> {
                int colorComparison = card1.getColour().compareTo(card2.getColour());
                return (colorComparison != 0) ? colorComparison : Integer.compare(card1.getValue(), card2.getValue());
            });
        }

        // For non-display mode, return a formatted string of card values
        if (!displayCardOptions) {
            StringBuilder printedCards = new StringBuilder("[");
            for (int i = 0; i < deck.size(); i++) {
                printedCards.append(deck.get(i).number).append(" of ").append(deck.get(i).colour);
                if (i < deck.size() - 1)
                    printedCards.append("; ");
            }
            printedCards.append("]");
            return printedCards.toString();
        }

        // Set console width 
        final int CONSOLE_WIDTH = 120;

        // Render ASCII art for all cards and display them horizontally
        List<List<String>> allCardsLines = new ArrayList<>();
        for (Card card : deck) {
            allCardsLines.add(card.renderCardAscii());
        }

        // Calculate card width 
        int cardWidth = 13;
        // Determine how many cards can fit per line
        int cardsPerLine = Math.max(1, CONSOLE_WIDTH / cardWidth);

        // Process cards in groups 
        for (int group = 0; group < allCardsLines.size(); group += cardsPerLine) {
            int groupEnd = Math.min(group + cardsPerLine, allCardsLines.size());
            
            if (!lineNumber) {
                // Add number above card for closedDeck
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
                // Add line number for parade
                System.out.println("Line " + (group / cardsPerLine + 1) + ":");

            }

            // Print all cards side by side
            for (int line = 0; line < 9; line++) {
                StringBuilder combinedLine = new StringBuilder();

                for (int i = group; i < groupEnd; i++) {
                    if (line < allCardsLines.get(i).size()) {
                        String cardLine = allCardsLines.get(i).get(line);
                        combinedLine.append(cardLine).append("  "); // Consistent 2-space separation
                    }
                }
                System.out.println(combinedLine.toString());
            }
        }
        return "";
    }
}