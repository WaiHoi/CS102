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
                    colorCode + "|   \\_=_/   |" + AnsiColors.RESET
                );
            case "red": // Clown
                return Arrays.asList(
                    colorCode + "|   *<:o)   |" + AnsiColors.RESET,
                    colorCode + "|  <(:o)>   |" + AnsiColors.RESET,
                    colorCode + "|   <:o*>   |" + AnsiColors.RESET
                );
            case "green": // Dwarf
                return Arrays.asList(
                    colorCode + "|   o==[]   |" + AnsiColors.RESET,
                    colorCode + "|  ( o_o )  |" + AnsiColors.RESET,
                    colorCode + "|   \\___/   |" + AnsiColors.RESET
                );
            case "orange": // Duck
                return Arrays.asList(
                    colorCode + "|   ~<')    |" + AnsiColors.RESET,
                    colorCode + "|  ~<')___  |" + AnsiColors.RESET,
                    colorCode + "|     \"     |" + AnsiColors.RESET
                );
            case "grey": // Bunny
                return Arrays.asList(
                    colorCode + "|   ('>     |" + AnsiColors.RESET,
                    colorCode + "|  /rr *\\)) |" + AnsiColors.RESET,
                    colorCode + "| ((`\\___   |" + AnsiColors.RESET
                );
            case "purple": // Cat
                return Arrays.asList(
                    colorCode + "|   /\\_/\\   |" + AnsiColors.RESET,
                    colorCode + "|  ( o.o )  |" + AnsiColors.RESET,
                    colorCode + "|   > ^ <   |" + AnsiColors.RESET
                );
            default: // Default empty art
                return Arrays.asList(
                    colorCode + "|           |" + AnsiColors.RESET,
                    colorCode + "|           |" + AnsiColors.RESET,
                    colorCode + "|           |" + AnsiColors.RESET
                );
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
    String topNumberLine = colorCode + String.format("| %-9s |", number) + AnsiColors.RESET; // Top number (left-aligned)
    String emptyLine = colorCode + "|           |" + AnsiColors.RESET;

    // Get the ASCII art for this card's color
    List<String> asciiArt = getAsciiArt(colour);

    String bottomNumberLine = colorCode + String.format("| %9s |", number) + AnsiColors.RESET; // Bottom number (right-aligned)

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
            case "red": return AnsiColors.BRIGHT_RED;
            case "blue": return AnsiColors.BRIGHT_BLUE;
            case "purple": return AnsiColors.BRIGHT_MAGENTA;
            case "green": return AnsiColors.BRIGHT_GREEN;
            case "orange": return AnsiColors.BRIGHT_YELLOW;
            default: return AnsiColors.BRIGHT_WHITE;
        }
    }

    public static String printCards(ArrayList<Card> deck, boolean sorting, boolean displayCardOptions) {
        if (displayCardOptions) {
            // Render ASCII art for all cards and display them horizontally
            List<List<String>> allCardsLines = new ArrayList<>();
            for (Card card : deck) {
                allCardsLines.add(card.renderCardAscii());
            }

            // Print each line of the cards side by side
            for (int line = 0; line < 9 ; line++) { // Each card has NINE lines
                StringBuilder combinedLine = new StringBuilder();
                for (List<String> cardLines : allCardsLines) {
                    if (line < cardLines.size()) {
                        combinedLine.append(cardLines.get(line)).append("  ");
                    }
                }
                System.out.println(combinedLine.toString());
            }
            return "";
        }

        if (deck.isEmpty()) {
            return "[]";
        }

        if (sorting) {
            deck.sort((card1, card2) -> {
                int colorComparison = card1.getColour().compareTo(card2.getColour());
                return (colorComparison != 0) ? colorComparison : Integer.compare(card1.getValue(), card2.getValue());
            });
        }

        // For non-display mode, return a formatted string of card values
        StringBuilder printedCards = new StringBuilder("[");
        for (int i = 0; i < deck.size(); i++) {
            printedCards.append(deck.get(i).number).append(" of ").append(deck.get(i).colour);
            if (i < deck.size() - 1) printedCards.append("; ");
        }
        printedCards.append("]");
        return printedCards.toString();
    }
}


// package cardgame.model;

// import java.util.*;
// import cardgame.utility.AnsiColors;

// import org.fusesource.jansi.AnsiConsole;
// import static org.fusesource.jansi.Ansi.DISABLE;
// import static org.fusesource.jansi.Ansi.ansi;

// public class Card {

//     private int number;
//     private String colour;

//     public Card(int n, String c) {
//         this.number = n;
//         this.colour = c;
//     }

//     public String getColour() {
//         return colour;
//     }

//     public int getValue() {
//         return number;
//     }

//     public static String printCards(ArrayList<Card> deck, boolean sorting, boolean displayCardOptions) {
//         if (displayCardOptions) {
//             int counter = 1;
//             for (Card card : deck) {
//                 String coloredText;
//                 switch (card.colour) {
//                     case "red":
//                         coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_RED);
//                         break;
//                     case "blue":
//                         coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_BLUE);
//                         break;
//                     case "purple":
//                         coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_MAGENTA);
//                         break;
//                     case "green":
//                         coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_GREEN);
//                         break;
//                     case "orange":
//                         coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_YELLOW);
//                         break;
//                     default:
//                         coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_WHITE);
//                     break;
//                 }
//                 System.out.println(coloredText);
//                 counter++;
//             }
//             return "";
//         }
    
//         if (deck.isEmpty()) {
//             return "[]";
//         }
    
//         if (sorting) {
//             deck.sort((card1, card2) -> {
//                 int colorComparison = card1.getColour().compareTo(card2.getColour());
//                 return (colorComparison != 0) ? colorComparison : Integer.compare(card1.getValue(), card2.getValue());
//             });
//         }
    
//         // Construct the formatted string with colors
//         StringBuilder printedCards = new StringBuilder("[");
//         for (int i = 0; i < deck.size(); i++) {
//             Card card = deck.get(i);
//             String coloredText;
    
//             switch (card.colour) {
//                 case "red":
//                     coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_RED);
//                     break;
//                 case "blue":
//                     coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_BLUE);
//                     break;
//                 case "purple":
//                     coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_MAGENTA);
//                     break;
//                 case "green":
//                     coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_GREEN);
//                     break;
//                 case "orange":
//                     coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_YELLOW);
//                     break;
//                 default:
//                     coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_WHITE);
//                     break;
//             }
    
//             printedCards.append(coloredText);
    
//             if (i < deck.size() - 1) {
//                 printedCards.append("; ");
//             }
//         }
    
//         printedCards.append("]");
//         return printedCards.toString();
//     }
         
// }
