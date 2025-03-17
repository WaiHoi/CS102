import java.util.*;

public class Score {

    /* 
    *  method 1: convert arraylist to hashmap
    *             -> easier counting
    *
    *  method 2: count player cards 
    *             -> count number of cards based on colour 
    *             -> returns number of cards and its colours 
    *             -> example: return [5,"red", 7, "blue"]
    *
    *  method 3: find highest number of cards for each number 
    *             -> use method 2  
    *             -> compare and find highest number 
    *             -> take note of tie 
    *
    *  method 4: calculate score
    *             -> if-else 
    *             -> if respective colour and number matches highest number
    *                  -> total + 1
    *             -> else 
    *                  -> get value of card and add to total 
    */
    
    /*** Method 1: Convert to HashMap ***/
    public HashMap<Integer, String> convertToHashMap(ArrayList<String> list) {
        HashMap<Integer, String> map = new HashMap<>();

        for (String i : list) {
            String[] s = i.split(",");
            String colour = s[1];
            int value = Integer.parseInt(s[0]);
            map.put(value, colour);
        }

        return map;
    }

}

