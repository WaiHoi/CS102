package cardgame.utility;

import java.util.*;
import java.util.regex.*;

import cardgame.utility.AnsiColors;

public class UsernameValidator {

    // Set to store unique usernames
    private static final Set<String> usernames = new HashSet<>();

    // Validation rules
    private static final Set<String> RESERVED_NAMES = Set.of("bot", "player");
    private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9_-]+$");
    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 20;

    /**
     * Validates a username based on predefined rules.
     *
     * @param username      The username to validate.
     * @param errorMessage  A StringBuilder to store error messages.
     * @return true if the username is valid, false otherwise.
     */
    public static synchronized boolean validateUsername(String username, StringBuilder errorMessage) {
        // Reset error message
        errorMessage.setLength(0);

        // Perform validation checks
        if (isNullOrEmpty(username, errorMessage)) return false;
        if (isInvalidLength(username, errorMessage)) return false;
        if (hasInvalidCharacters(username, errorMessage)) return false;
        if (isReservedName(username, errorMessage)) return false;
        if (!isUnique(username, errorMessage)) return false;

        // If all checks pass, add the username to the set and return true
        usernames.add(username);
        return true;
    }

    /**
     * Removes a username from the set of stored usernames.
     *
     * @param username The username to remove.
     */
    public static synchronized void removeUsername(String username) {
        if (username != null) {
            usernames.remove(username);
        }
    }

    /**
     * Clears all stored usernames.
     */
    public static synchronized void clearAllUsernames() {
        usernames.clear();
    }

    // Helper Methods

    /**
     * Checks if a username is null or empty.
     */
    private static boolean isNullOrEmpty(String username, StringBuilder errorMessage) {
        if (username == null || username.isBlank()) {
            appendError(errorMessage, "Username cannot be empty.");
            return true;
        }
        return false;
    }

    /**
     * Checks if a username's length is invalid.
     */
    private static boolean isInvalidLength(String username, StringBuilder errorMessage) {
        if (username.length() < MIN_LENGTH) {
            appendError(errorMessage, "Username too short. Must be between 3 and 20 characters.");
            return true;
        }
        if (username.length() > MAX_LENGTH) {
            appendError(errorMessage, "Username too long. Must be between 3 and 20 characters.");
            return true;
        }
        return false;
    }

    /**
     * Checks if a username contains invalid characters.
     */
    private static boolean hasInvalidCharacters(String username, StringBuilder errorMessage) {
        if (!VALID_CHARS.matcher(username).matches()) {
            appendError(errorMessage, "Username contains invalid characters. Only letters, numbers, hyphens (-), and underscores (_) are allowed.");
            return true;
        }
        return false;
    }

    /**
     * Checks if a username is a reserved name.
     */
    private static boolean isReservedName(String username, StringBuilder errorMessage) {
        if (RESERVED_NAMES.contains(username.toLowerCase())) {
            appendError(errorMessage, "Username is reserved. Please choose another.");
            return true;
        }
        return false;
    }

    /**
     * Checks if a username is unique.
     */
    private static boolean isUnique(String username, StringBuilder errorMessage) {
        if (!usernames.add(username)) {
            appendError(errorMessage, "Username already in use.");
            return false;
        }
        return true;
    }

    /**
     * Appends an error message in bright red with typical formatting to the provided StringBuilder.
     */
    private static void appendError(StringBuilder errorMessage, String message) {
        String formattedError = AnsiColors.colorizeBold("ERROR: " + message, AnsiColors.BRIGHT_RED);
        errorMessage.append(formattedError);
    }
}


// package cardgame.utility;

// import java.util.*;
// import java.util.regex.*;

// import org.fusesource.jansi.AnsiConsole;
// import static org.fusesource.jansi.Ansi.DISABLE;
// import static org.fusesource.jansi.Ansi.ansi;

// public class UsernameValidator {
//     private static final Set<String> usernames = new HashSet<>();

//     // validation rules
//     private static final Set<String> reservedNames = Set.of(
//         "bot", "player"
//     );
//     private static final Pattern validChars = Pattern.compile("^[a-zA-Z0-9_-]+$");
//     private static final int minLength = 3;
//     private static final int maxLength = 20;

//     public static synchronized boolean validateUsername(String username, StringBuilder errorMessage) {

//         // reset error message
//         errorMessage.setLength(0);

//         // check if null or empty
//         if (username == null || username.isBlank()) {
//             errorMessage.append("Username cannot be empty");
//             return false;
//         }

//         // check length
//         if (username.length() < 3) {
//             errorMessage.append("Username too short (between 3 to 20 characters)");
//             return false;
//         }
//         if (username.length() > 20) {
//             errorMessage.append("Username too long (between 3 to 20 characters)");
//             return false;

//         }

//         // check valid characters
//         if (!validChars.matcher(username).matches()) {
//             errorMessage.append("Only letters, numbers, hyphens (-), and underscores (_) allowed");
//             return false;
//         }

//         // check reserved names
//         if (reservedNames.contains(username.toLowerCase())) {
//             errorMessage.append("Reserved username - choose another");
//             return false;
//         }

//         // check uniqueness
//         if (!usernames.add(username)) {
//             errorMessage.append("Username already in use");
//             return false;
//         }

//         return true;
//     }

//     public static synchronized void removeUsername(String username) {
//         if (username != null) {
//             usernames.remove(username);
//         }
//     }

//     public static synchronized void clearAllUsernames() {
//         usernames.clear();
//     }

// }
