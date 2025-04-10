package cardgame.utility;

import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.DISABLE;
import static org.fusesource.jansi.Ansi.ansi;

public class AnsiColors {
    // ANSI Reset
    public static final String RESET = "\u001B[0m";

    // ANSI Bright Colors
    public static final String BRIGHT_BLACK = "\u001B[90m";
    public static final String BRIGHT_RED = "\u001B[91m";
    public static final String BRIGHT_GREEN = "\u001B[92m";
    public static final String BRIGHT_YELLOW = "\u001B[93m";
    public static final String BRIGHT_BLUE = "\u001B[94m";
    public static final String BRIGHT_MAGENTA = "\u001B[95m";
    public static final String BRIGHT_CYAN = "\u001B[96m";
    public static final String BRIGHT_WHITE = "\u001B[97m";

    // ANSI Bold
    public static final String BOLD = "\u001B[1m";

    // ANSI Underline
    public static final String UNDERLINE = "\u001B[4m";

    // Utility method to apply color
    public static String colorize(String text, String color) {
        return color + text + RESET;
    }

    public static String colorizeBold(String text, String color) {
        return BOLD + color + text + RESET;
    }
}
