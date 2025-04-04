package cardgame.io.output;

public interface GameOutput {
    
    /* ===== CORE METHODS ===== */
    
    // adds new line
    void println(String message);

    // no new line
    void print(String message);

    // formatted output
    void printf(String format, Object... args);

    /* ===== SPECIFIC METHOD ===== */

    // [SERVER]
    void sendServer(String message);

    // [ERROR]
    void sendError(String message);

    // [CHAT]
    void sendChat(String message);

    // [PRIVATE]
    void sendPrivate(String message);

    /* ===== BROADCAST MESSAGES ===== */
    // all players  
    void broadcastToAll(String message);
    
}
