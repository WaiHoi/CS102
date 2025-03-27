package cardgame;

import cardgame.utility.Initialize;
import cardgame.game.Game;

import org.fusesource.jansi.AnsiConsole;
import static org.fusesource.jansi.Ansi.ansi;

public class Main {
    public static void main(String[] args) {
        
        AnsiConsole.systemInstall();

        Initialize.displayMenu();

        Initialize.initializeVariables();

        Game.mainFunction();

        AnsiConsole.systemUninstall();
        // Score score = new Score();
        // score.isWinner(g.players, g);
        

    }
}
