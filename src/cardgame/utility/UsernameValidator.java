package cardgame.utility;

import java.util.*;
import java.util.regex.*;

public class UsernameValidator {
    private static final Set<String> usernames = new HashSet<>();

    // validation rules
    private static final Set<String> reservedNames = Set.of(
        "bot", "system", "admin", "player", "guest", "computer", "ai"
    );
    private static final Pattern validChars = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final int minLength = 3;
    private static final int maxLength = 20;

    public static synchronized boolean validateUsername(String username, StringBuilder errorMessage) {

        // reset error message
        errorMessage.setLength(0);

        // check if null or empty
        if (username == null || username.isBlank()) {
            errorMessage.append("Username cannot be empty");
            return false;
        }

        // check length
        if (username.length() < 3) {
            errorMessage.append("Username too short (between 3 to 20 characters)");
            return false;
        }
        if (username.length() > 20) {
            errorMessage.append("Username too long (between 3 to 20 characters)");
            return false;

        }

        // check valid characters
        if (!validChars.matcher(username).matches()) {
            errorMessage.append("Only letters, numbers, hyphens (-), and underscores (_) allowed");
            return false;
        }

        // check reserved names
        if (reservedNames.contains(username.toLowerCase())) {
            errorMessage.append("Reserved username - choose another");
            return false;
        }

        // check uniqueness
        if (!usernames.add(username)) {
            errorMessage.append("Username already in use");
            return false;
        }

        return true;
    }

    public static synchronized void removeUsername(String username) {
        if (username != null) {
            usernames.remove(username);
        }
    }

    public static synchronized void clearAllUsernames() {
        usernames.clear();
    }

}
