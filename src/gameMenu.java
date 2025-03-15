import java.util.*;

public class gameMenu {
    public int numPlayers;
    public int numBots;

    public void displayMenu() {
        numPlayers = 0;
        numBots = 0;

        while (numPlayers + numBots < 2 || numPlayers + numBots > 6) {
            try {
                Scanner sc = new Scanner(System.in);
                System.out.print("Enter number of players: ");
                numPlayers = sc.nextInt();

                sc = new Scanner(System.in);
                System.out.print("Enter number of bots: ");
                numBots = sc.nextInt();

                if (numPlayers + numBots < 2 || numPlayers + numBots > 6) {
                    System.out.println("Total number of players and bots should be between 2 and 6!\n");
                }
            } catch (InputMismatchException e) {
                System.out.println("Input must be an integer!\n");
            }
            
        }
    }
}