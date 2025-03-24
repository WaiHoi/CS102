public class main {
    public static void main(String[] args) {
        gameMenu input = new gameMenu();
        input.displayMenu();

        Game g = new Game(input.numPlayers, input.numBots);
        // for (int i = 0; i < g.deck.size(); i++) {
        //     System.out.println(g.deck.get(i).colour + "," + g.deck.get(i).number);
        // }

        GameLoop gl = new GameLoop(g.players, g.deck, g.parade);
        gl.mainFunction();
    }
}
