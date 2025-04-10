package cardgame.model;

import java.util.*;
import org.fusesource.jansi.AnsiConsole;
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

    public static String printCards(ArrayList<Card> deck, boolean sorting, boolean displayCardOptions){
        if (displayCardOptions) {
            int counter = 1;
            for(Card card : deck) {
                // System.out.println(counter + ". " + card.colour + " " + card.number);
                
                switch (card.colour) {
                    case "red":
                        System.out.println(ansi().fgBrightRed().a(counter + ". " + card.colour + " " + card.number).reset());
                        break;
                    case "blue":
                        System.out.println(ansi().fgBrightBlue().a(counter + ". " + card.colour + " " + card.number).reset());
                        break;
                    case "purple":
                        System.out.println(ansi().fgBrightMagenta().a(counter + ". " + card.colour + " " + card.number).reset());
                        break;
                    case "green":
                        System.out.println(ansi().fgBrightGreen().a(counter + ". " + card.colour + " " + card.number).reset());
                        break;
                    case "orange":
                        System.out.println(ansi().fgBrightYellow().a(counter + ". " + card.colour + " " + card.number).reset());
                        break;
                    default:
                        System.out.println(counter + ". " + card.colour + " " + card.number);
                        break;
                }
                counter ++;
            }
            return "";
        }

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

}
