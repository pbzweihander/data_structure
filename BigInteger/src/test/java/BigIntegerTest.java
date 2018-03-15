
/*
 * Test code written by kipa00 and modified by pbzweihander.
 * All right reserved by kipa00.
 * https://site.thekipa.com/charsnine/
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BigIntegerTest {
    public static final long SEC = 1000000000L, MSEC = 1000000L;
    public static final long LIMIT = 2 * SEC;

    public static final String FILE_NAME = "build/classes/java/main/BigInteger.class";
    public static final List<String> BANNED_CLASSES = Arrays.asList(new String[] { "java/util/ArrayList",
            "java/util/Vector", "java/util/LinkedList", "java/math/BigInteger", "java/math/BigDecimal" });

    public static final int SHORT_TEST_COUNT = 1000000;

    @Test
    public void clarity() throws Exception {
        Analyzer an = new Analyzer(FILE_NAME);
        int i;
        for (i = 0; i < an.pool.length; ++i) {
            Analyzer.Pool ap = an.pool[i];
            if (ap instanceof Analyzer.MethodPool) {
                ap = an.pool[((Analyzer.MethodPool) ap).v];
            }
            if (ap instanceof Analyzer.ClassPool) {
                String cname = ((Analyzer.UTFPool) an.pool[((Analyzer.ClassPool) ap).v]).toString(an);
                assertTrue("The class " + FILE_NAME + " uses banned class " + cname.replace("/", "."),
                        !BANNED_CLASSES.contains(cname));
            }
        }
    }

    @Test
    public void checkWithShortInteger() {
        Random random = new Random();

        String[] signs = { "+", "-", "" };
        char[] ops = { '+', '-', '*' };
        String spaces = "                                    ";

        Iterator<Integer> randNum = random.ints().filter(i -> i >= 0).iterator();
        Iterator<String> randSign = random.ints(0, signs.length).boxed().map(i -> signs[i]).iterator();
        Iterator<Character> randOp = random.ints(0, ops.length).boxed().map(i -> ops[i]).iterator();
        Iterator<String> randSpaces = random.ints(0, spaces.length()).boxed().map(len -> spaces.substring(0, len))
                .iterator();

        for (int i = 0; i < SHORT_TEST_COUNT; i++) {
            int num1 = randNum.next();
            int num2 = randNum.next();
            String s1 = randSign.next();
            String s2 = randSign.next();
            char op = randOp.next();

            int signed1 = num1 * (s1.equals("-") ? -1 : 1);
            int signed2 = num2 * (s2.equals("-") ? -1 : 1);

            long res = 0;

            switch (op) {
            case '+':
                res = (long) signed1 + (long) signed2;
                break;
            case '-':
                res = (long) signed1 - (long) signed2;
                break;
            case '*':
                res = (long) signed1 * (long) signed2;
                break;
            }

            String expr = randSpaces.next() + s1 + randSpaces.next() + num1 + randSpaces.next() + op + randSpaces.next()
                    + s2 + randSpaces.next() + num2 + randSpaces.next();

            BigInteger resBI = BigInteger.evaluate(expr);

            assertEquals("Wrong answer on test " + i + ".\nexpr : " + expr, "" + res, resBI.toString());
        }
    }

    @Test
    public void checkTestCases() throws Exception {
        int i;
        File in, out;
        for (i = 1; (in = new File("data/i" + i + ".in")).exists()
                && (out = new File("data/o" + i + ".out")).exists(); ++i) {
            BufferedReader inputReader = new BufferedReader(new FileReader(in));
            BufferedReader outputReader = new BufferedReader(new FileReader(out));

            String input;
            String actualAnswer;
            String expectedAnswer;
            while (!(input = inputReader.readLine()).equals("quit")) {
                actualAnswer = BigInteger.evaluate(input).toString();
                expectedAnswer = outputReader.readLine().trim();

                assertEquals("Wrong answer on test " + i + ".\nexpr : " + input, expectedAnswer, actualAnswer);
            }

            inputReader.close();
            outputReader.close();
        }

        if (--i == 0)
            fail("No test cases found!");
    }
}
