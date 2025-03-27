package cardgame.model;
import cardgame.game.*;
import java.util.Random;

public class Bot extends Player {
    public Bot(String name){
        super(name);
    }

    public int placeCard(){
        Random rand = new Random();
            int selectNumber = rand.nextInt(closedDeck.size());
            return selectNumber;
    }
}
