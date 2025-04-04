package cardgame.io.input;

import java.util.*;

public class ConsoleInput implements GameInput {
    private final Scanner sc = new Scanner(System.in);

    @Override
    // read line
    public String readLine(String prompt) {
        System.out.print(prompt);
        return sc.nextLine();
    }

    @Override
    // read number
    public int readInt(String prompt, int min, int max) {
        
        while (true) {
            System.out.print(prompt);

            try {
                int value = sc.nextInt();
                sc.nextLine();
                
                if (value >= min && value <= max) {
                    return value;
                }

                // error handling 
                System.out.printf("Input must be between %d and %d\n", min, max);

            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                // clear invalid input
                sc.nextLine();
            }
        }


    }
}
