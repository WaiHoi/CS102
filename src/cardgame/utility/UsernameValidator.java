package cardgame.utility;

import java.util.*;

public class UsernameValidator {
    
    private static final Set<String> usernames = new HashSet<>();

    public static synchronized boolean checkUsername(String requestedUsername) {

        if (requestedUsername == null || requestedUsername.isEmpty()) {
            return false;
        }
        // will return false if name exists
        return usernames.add(requestedUsername);

    } 

    public static synchronized void removeUsername(String username) {
        usernames.remove(username);
    }

}
