package cardgame.utility;

import java.util.*;
import cardgame.game.*;

/**
 * Comparator to compare two players based on their score and, if needed, their number of cards.
 * This is used to sort players at the end of the game:
 * - Compare based on score
 * - If scores are equal, compare number of cards
 */
public class ScoreCardComparator implements Comparator<Player>{
    public int compare(Player p1, Player p2){
        // Compare players based on score (by lower score)
        int scoreComparison = Integer.compare(p1.playerScoreCount, p2.playerScoreCount);

        // Return if scores are different
        if (scoreComparison != 0) {
            return scoreComparison;
        }

        // Equal score, compare number of cards
        return Integer.compare(p1.openDeck.size(), p2.openDeck.size());
    }
}
