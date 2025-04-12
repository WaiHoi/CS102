package cardgame.utility;

import java.util.*;
import cardgame.game.*;


public class ScoreCardComparator implements Comparator<Player>{
    public int compare(Player p1, Player p2){
        int cmp = p1.playerScoreCount - p2.playerScoreCount;

        if (cmp != 0) {
            return cmp;
        }

        return p1.openDeck.size() - p2.openDeck.size();
    }
}
