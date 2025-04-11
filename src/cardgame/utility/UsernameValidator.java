package cardgame.utility;

import java.util.*;
import java.util.regex.*;

import cardgame.utility.AnsiColors;

public class UsernameValidator {

    // Set to store unique usernames
    private static final Set<String> usernames = new HashSet<>();

    // Validation rules
    private static final Set<String> RESERVED_NAMES = Set.of("bot", "player");
    private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9_-]+$"); // Matches full string: ^ = start, [a-zA-Z0-9_-] = allowed chars, + = 1+ times, $ = end
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
        if (isNullOrEmpty(username, errorMessage)) return false; //line 65
        if (isInvalidLength(username, errorMessage)) return false;//line 76
        if (hasInvalidCharacters(username, errorMessage)) return false;//line 91
        if (isReservedName(username, errorMessage)) return false;//line 102
        if (!isUnique(username, errorMessage)) return false;//line 113

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
