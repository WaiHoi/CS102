public class main {
    public static void main(String[] args) {
        gameMenu input = new gameMenu();
        input.displayMenu();

        Game g = new Game(input.numPlayers, input.numBots);

        // instantiate gameloop details
        GameLoop gl = new GameLoop(g.players, g.deck, g.parade);

        // run main function
        gl.mainFunction();
    }
}
