package cardgame.model;
import cardgame.GameMenu;
import cardgame.game.*;
import cardgame.network.ClientHandler;

import java.util.*;

public class Human extends Player {
    public Card card;
    private int playerID;

    public Human(String name) {
        super(name);

        // set for local players
        this.playerID = -1;
    }

    public Human(String name, int playerID) {
        super(name);
        this.playerID = playerID;
    }

    public int placeCard() {

        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + "Your closed deck:");
        ClientHandler.gameOutput(ClientHandler.TAG_PRIVATE + Card.printCards(closedDeck, false, true));
        ClientHandler.gameOutputRaw(ClientHandler.TAG_PRIVATE + "Please choose a card to be added into the parade: ");

        int selectNumber = 0;

        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                selectNumber = sc.nextInt() - 1;
                sc.nextLine();

                if (selectNumber >= 0 && selectNumber <= 4) {
                    break; // valid input, exit loop
                } else {
                    ClientHandler.gameOutput(ClientHandler.TAG_ERROR + "Invalid Entry!");
                    ClientHandler.gameOutputRaw(ClientHandler.TAG_PRIVATE + "Please enter only numbers from 1 to 5:");
                }

            } catch (InputMismatchException e) {
                sc.nextLine();
                ClientHandler.gameOutput(ClientHandler.TAG_ERROR + "Invalid Entry!");
                ClientHandler.gameOutputRaw(ClientHandler.TAG_PRIVATE + "Please enter only numbers from 1 to 5:");
            }
        }
        return selectNumber;
    }

    public static void initializePlayers(){
        for (int i = 1; i < GameMenu.numHumans; i++) {
            Player p = new Human(GameMenu.usernames.get(i));
            Player.players.add(p); 
        }
    }
    
}
