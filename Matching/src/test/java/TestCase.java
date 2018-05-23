import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import org.junit.Test;

public class TestCase extends Matching {
    @Test
    public void testWithTestCases() throws FileNotFoundException, IOException {
        PrintStream originalStdOut = System.out;
        File inFile, expectedOutFile, actualOutFile;
        actualOutFile = new File(".out");
        int i = 0;

        for (inFile = new File("test/in" + i + ".txt"), expectedOutFile = new File("test/out" + i + ".txt"); inFile
                .exists()
                && expectedOutFile.exists(); inFile = new File("test/in" + (++i) + ".txt"), expectedOutFile = new File(
                        "test/out" + i + ".txt")) {
            PrintWriter actualOutFileWriter = new PrintWriter(actualOutFile);
            actualOutFileWriter.print("");
            actualOutFileWriter.close();

            BufferedReader inReader = new BufferedReader(new FileReader(inFile));

            Matching matching = new Matching();

            String input;

            input = inReader.readLine();
            processLine(matching, input);

            BufferedOutputStream actualOutStream = new BufferedOutputStream(new FileOutputStream(actualOutFile));
            PrintStream actualOutPrintStream = new PrintStream(actualOutStream, true);

            System.setOut(actualOutPrintStream);
            while (((input = inReader.readLine()) != null) && !input.equals("QUIT"))
                processLine(matching, input);
            System.setOut(originalStdOut);

            inReader.close();
            actualOutStream.close();

            BufferedReader expectedOutReader = new BufferedReader(new FileReader(expectedOutFile));
            BufferedReader actualOutReader = new BufferedReader(new FileReader(actualOutFile));

            int lineNumber = 0;
            String expected, actual;
            while (((expected = expectedOutReader.readLine()) != null)
                    && ((actual = actualOutReader.readLine()) != null)) {
                assert expected.equals(actual) : "Test case " + i + " line " + lineNumber + "\nexpected: " + expected
                        + "\nactual: " + actual;
                lineNumber++;
            }

            expectedOutReader.close();
            actualOutReader.close();
        }
    }
}
