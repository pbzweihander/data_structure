import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.IllegalThreadStateException;
import java.lang.Process;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;

public class Kipa {

    public static final long sec = 1000000000L, msec = 1000000L;
    public static final long limit = 2 * sec;

    public static final List<String> bannedClasses = Arrays
            .asList(new String[] { "java/util/ArrayList", "java/util/Vector", "java/util/LinkedList" });

    public static final String CLASS_PATH = "./build/classes/java/main/";
    public static final String CLASS_NAME = "MovieDatabaseConsole";

    @Test
    public void testWithTimeLimit() throws Exception {
        int i;
        File in, out;
        long mx = -1;
        for (i = 1; (in = new File("data/i" + i + ".in")).exists()
                && (out = new File("data/o" + i + ".out")).exists(); ++i) {
            System.out.print("\rtest case #" + i + "...");
            ProcessBuilder pb = new ProcessBuilder("java", "-Xms128M", "-Xmx128M", CLASS_NAME);
            pb.directory(new File(CLASS_PATH));
            pb.redirectInput(in);
            pb.redirectOutput(new File("y.out"));
            Process p = pb.start();
            long st = System.nanoTime(), ed;
            while ((ed = System.nanoTime()) - st < limit) {
                try {
                    assert p.exitValue() == 0 : "Runtime error on test " + i;
                    break;
                } catch (IllegalThreadStateException err) {
                }
            }
            mx = ed - st > mx ? ed - st : mx;

            assert ed - st < limit : "Time limit exceeded on test " + i;

            BufferedReader br = new BufferedReader(new FileReader(new File("y.out")));
            BufferedReader br2 = new BufferedReader(new FileReader(out));
            String s, s2;
            while ((s2 = br2.readLine()) != null) {
                s = br.readLine();
                assert s2.equals(s) : "Wrong answer on test " + i + "\nexpected: " + s2 + "\nactual: " + s;
            }
            br.close();
            br2.close();
        }

        assert --i != 0 : "No test cases found!";

        System.out.println("\rYou passed all " + i + " test case" + (i == 1 ? "" : "s") + "! (" + (mx / msec) + "ms)");
    }

    @Test
    public void clarityTest() throws Exception {
        Analyzer an = new Analyzer(CLASS_PATH + CLASS_NAME + ".class");
        int i;
        for (i = 0; i < an.pool.length; ++i) {
            Analyzer.Pool ap = an.pool[i];
            if (ap instanceof Analyzer.MethodPool) {
                ap = an.pool[((Analyzer.MethodPool) ap).v];
            }
            if (ap instanceof Analyzer.ClassPool) {
                String cname = ((Analyzer.UTFPool) an.pool[((Analyzer.ClassPool) ap).v]).toString(an);
                assert !bannedClasses.contains(cname) : "uses banned class " + cname.replace("/", ".");
            }
        }
    }
}
