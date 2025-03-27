package cardgame;

import cardgame.utility.Initialize;
import cardgame.game.Game;

public class Main {
    public static void main(String[] args) {
        
        Initialize.displayMenu();

        Initialize.initializeVariables();

        Game.mainFunction();


        // Score score = new Score();
        // score.isWinner(g.players, g);
        

    }
}
