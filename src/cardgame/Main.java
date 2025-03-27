package cardgame;

import cardgame.utility.*;
import cardgame.game.*;

public class Main {
    public static void main(String[] args) {
        
        Initialize.displayMenu();

        Initialize.initializeVariables();

        Game.mainFunction();


        // Score score = new Score();
        // score.isWinner(g.players, g);
        

    }
}
