package cardgame.model;

import java.util.*;
import cardgame.utility.AnsiColors;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.DISABLE;
import static org.fusesource.jansi.Ansi.ansi;

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

    public static String printCards(ArrayList<Card> deck, boolean sorting, boolean displayCardOptions) {
        if (displayCardOptions) {
            int counter = 1;
            for (Card card : deck) {
                String coloredText;
                switch (card.colour) {
                    case "red":
                        coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_RED);
                        break;
                    case "blue":
                        coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_BLUE);
                        break;
                    case "purple":
                        coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_MAGENTA);
                        break;
                    case "green":
                        coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_GREEN);
                        break;
                    case "orange":
                        coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_YELLOW);
                        break;
                    default:
                        coloredText = AnsiColors.colorizeBold(counter + ". " + card.number + " of " + card.colour, AnsiColors.BRIGHT_WHITE);
                    break;
                }
                System.out.println(coloredText);
                counter++;
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
    
        // Construct the formatted string with colors
        StringBuilder printedCards = new StringBuilder("[");
        for (int i = 0; i < deck.size(); i++) {
            Card card = deck.get(i);
            String coloredText;
    
            switch (card.colour) {
                case "red":
                    coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_RED);
                    break;
                case "blue":
                    coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_BLUE);
                    break;
                case "purple":
                    coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_MAGENTA);
                    break;
                case "green":
                    coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_GREEN);
                    break;
                case "orange":
                    coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_YELLOW);
                    break;
                default:
                    coloredText = AnsiColors.colorizeBold(card.number + " of " + card.colour, AnsiColors.BRIGHT_WHITE);
                    break;
            }
    
            printedCards.append(coloredText);
    
            if (i < deck.size() - 1) {
                printedCards.append("; ");
            }
        }
    
        printedCards.append("]");
        return printedCards.toString();
    }
         
}
