
/*
 * Test cases are made by kipa00
 * All right reserved to kipa00
 * site.thekipa.com/charsnine
 */

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class SubwayTest {
    public static class TestCase {
        private final String testName;
        private final FileReader data;
        private final FileReader testIn;
        private final FileReader testOut;

        public TestCase(String testCaseName, String dataName) {
            testName = testCaseName + "@" + dataName;
            try {
                testIn = new FileReader(new File("tc/" + testCaseName + ".in"));
                testOut = new FileReader(new File("tc/" + testCaseName + ".out"));
                data = new FileReader(new File("data/" + dataName));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return testName;
        }

        public FileReader getData() {
            return data;
        }

        public FileReader getIn() {
            return testIn;
        }

        public FileReader getOut() {
            return testOut;
        }

        public void close() throws IOException {
            testIn.close();
            testOut.close();
            data.close();
        }
    }

    private void testWithTestCase(TestCase testCase) throws FileNotFoundException, IOException {
        String testName = testCase.getName();
        File actualOutFile = new File(".out");
        PrintStream originalStdOut = System.out;

        PrintWriter actualOutFileCleaner = new PrintWriter(actualOutFile);
        actualOutFileCleaner.print("");
        actualOutFileCleaner.close();

        BufferedReader inReader = new BufferedReader(testCase.getIn());

        Subway subway = new Subway(testCase.getData());

        BufferedOutputStream actualOutStream = new BufferedOutputStream(new FileOutputStream(actualOutFile));
        PrintStream alternativeStdOut = new PrintStream(actualOutStream);

        System.setOut(alternativeStdOut);
        try {
            String input;
            while ((input = inReader.readLine()) != null)
                Subway.processLine(subway, input);
        } finally {
            System.setOut(originalStdOut);
        }

        inReader.close();
        alternativeStdOut.close();

        BufferedReader expectedOutReader = new BufferedReader(testCase.getOut());
        BufferedReader actualOutReader = new BufferedReader(new FileReader(actualOutFile));

        int lineNumber = 1;
        String expected, actual;
        while (((expected = expectedOutReader.readLine()) != null) && ((actual = actualOutReader.readLine()) != null)) {
            assert expected.equals(actual) : "Test case " + testName + " line " + lineNumber + "\nexpected: " + expected
                    + "\nactual: " + actual;
            lineNumber++;
        }

        expectedOutReader.close();
        actualOutReader.close();
    }

    public List<TestCase> getTestList() throws FileNotFoundException, IOException {
        File listFile = new File("test.list");

        BufferedReader reader = new BufferedReader(new FileReader(listFile));
        List<TestCase> list = reader.lines().map(line -> line.trim())
                .filter(line -> !line.isEmpty() && !line.startsWith("#")).map(line -> line.split(" "))
                .filter(arr -> arr.length == 2).map(arr -> new TestCase(arr[0], arr[1])).collect(Collectors.toList());
        reader.close();
        return list;
    }

    @Test
    public void testWithTestCases() throws FileNotFoundException, IOException {
        List<TestCase> tcs = getTestList();

        for (TestCase tc : tcs)
            testWithTestCase(tc);
    }
}
