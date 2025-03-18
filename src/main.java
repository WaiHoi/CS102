public class main {
    public static void main(String[] args) {
        gameMenu input = new gameMenu();
        input.displayMenu();

        game g = new game(input.numPlayers, input.numBots);
        // for (int i = 0; i < g.deck.size(); i++) {
        //     System.out.println(g.deck.get(i).colour + "," + g.deck.get(i).number);
        // }

        Player p = g.players.get(1);
        System.out.println(p.anonDeck.get(1).colour + p.anonDeck.get(1).number);
    }
}
