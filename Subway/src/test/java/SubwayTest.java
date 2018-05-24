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

public class SubwayTest {
    private void testWithTestCase(File dataFile) throws FileNotFoundException, IOException {
        String dataName = dataFile.getName();
        File inFile = new File("tc/" + dataName + ".in");
        File expectedOutFile = new File("tc/" + dataName + ".out");
        File actualOutFile = new File(".out");
        PrintStream originalStdOut = System.out;

        PrintWriter actualOutFileCleaner = new PrintWriter(actualOutFile);
        actualOutFileCleaner.print("");
        actualOutFileCleaner.close();

        BufferedReader inReader = new BufferedReader(new FileReader(inFile));

        Subway subway = new Subway(new FileReader(dataFile));

        BufferedOutputStream actualOutStream = new BufferedOutputStream(new FileOutputStream(actualOutFile));
        PrintStream alternativeStdOut = new PrintStream(actualOutStream);

        System.setOut(alternativeStdOut);
        String input;
        while ((input = inReader.readLine()) != null)
            Subway.processLine(subway, input);
        System.setOut(originalStdOut);

        inReader.close();
        alternativeStdOut.close();

        BufferedReader expectedOutReader = new BufferedReader(new FileReader(expectedOutFile));
        BufferedReader actualOutReader = new BufferedReader(new FileReader(actualOutFile));

        int lineNumber = 1;
        String expected, actual;
        while (((expected = expectedOutReader.readLine()) != null) && ((actual = actualOutReader.readLine()) != null)) {
            assert expected.equals(actual) : "Test case " + dataName + " line " + lineNumber + "\nexpected: " + expected
                    + "\nactual: " + actual;
            lineNumber++;
        }

        expectedOutReader.close();
        actualOutReader.close();
    }

    @Test
    public void testWithTestCases() throws FileNotFoundException, IOException {
        File dataFolder = new File("data");
        File[] dataFiles = dataFolder.listFiles();

        for (File data : dataFiles)
            testWithTestCase(data);
    }
}
