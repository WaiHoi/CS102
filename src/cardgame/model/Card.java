package cardgame.model;

import java.util.*;

public class Card {

    public int number;
    public String colour;

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

    public static String printCards(ArrayList<Card> deck, boolean sorting, boolean isPlayer){
        if(isPlayer){
            int counter = 1;
            for(Card card : deck){
                System.out.println(counter + ". " + card.colour + " " + card.number);
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
