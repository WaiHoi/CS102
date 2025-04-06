package cardgame.io.output;

public class ConsoleOutput implements GameOutput {

    /* ===== CORE METHODS ===== */
    
    @Override
    // adds new line
    public void println(String message) {
        System.out.println(message);

    }

    @Override
    // no new line
    public void print(String message) {
        System.out.print(message);

    }

    @Override
    // formatted output
    public void printf(String format, Object... args) {
        System.out.printf(format, args);

    }

    /* ===== SPECIFIC METHOD ===== */

    @Override
    // [SERVER]
    public void sendServer(String message) {
        System.out.println(message);

    }

    @Override
    public void sendError(String message) {
        System.out.println(message);
    }

    @Override
    public void sendChat(String message) {
        System.out.println(message);
    }

    @Override
    public void sendPrivate(String message) {
        System.out.println(message);
    }

    @Override
    // all players
    public void broadcastToAll(String message) {
        System.out.println(message);

    }

}
