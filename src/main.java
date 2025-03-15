public class main {
    public static void main(String[] args) {
        gameMenu input = new gameMenu();
        input.displayMenu();

        game g = new game();
        g.initialise();
        for (int i = 0; i < g.deck.size(); i++) {
            System.out.println(g.deck.get(i).colour + g.deck.get(i).number);
        }
    }
}
