package cardgame.io.input;

public interface GameInput {    
    // read line
    String readLine(String prompt);

    // read number
    int readInt(String prompt, int min, int max);
}
