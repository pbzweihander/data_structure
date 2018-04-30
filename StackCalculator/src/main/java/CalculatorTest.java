import java.io.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class CalculatorTest {
    public static void main(String args[]) {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            try {
                String input = br.readLine();
                if (input.compareTo("q") == 0)
                    break;

                CalculatorTest instance = new CalculatorTest();
                instance.processInput(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processInput(String input) {
        try {
            Parser parser = new Parser(input);
            Token[] tokens = parser.parse();

            Evaluator evaluator = new Evaluator(tokens);
            long result = evaluator.evaluate();

            System.out.print(Arrays.stream(tokens).map(t -> t.toString()).collect(Collectors.joining(" ")));
            System.out.println();
            System.out.println(result);
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR");
        }
    }
}
