import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.IllegalThreadStateException;
import java.lang.Process;
import org.junit.Test;

public class Kipa {

    public static final long sec = 1000000000L, msec = 1000000L;
    public static final long limit = 2 * sec;

    public static final String CLASS_PATH = "./build/classes/java/main/";
    public static final String CLASS_NAME = "CalculatorTest";

    @Test
    public void testWithTimeLimit() {
        int i;
        File in, out;
        long mx = -1;
        for (i = 1; (in = new File("data/i" + i + ".in")).exists()
                && (out = new File("data/o" + i + ".out")).exists(); ++i) {
            try {
                System.out.print("\rtest case #" + i + "...");
                ProcessBuilder pb = new ProcessBuilder("java", "-Xms128M", "-Xmx128M", CLASS_NAME);
                pb.directory(new File(CLASS_PATH));
                pb.redirectInput(in);
                pb.redirectErrorStream(true);
                pb.redirectOutput(new File("y.out"));
                pb.redirectError(new File("e.out"));
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
                if (ed - st >= limit) {
                    p.destroy();
                    throw new AssertionError("Time limit exceeded on test " + i);
                }
                BufferedReader br = new BufferedReader(new FileReader(new File("y.out")));
                BufferedReader br2 = new BufferedReader(new FileReader(out));
                String s, s2;
                int line_number = 1;
                while ((s = br.readLine()) != null) {
                    s2 = br2.readLine();
                    assert s.equals(s2) : "Wrong answer on test " + i + " - " + line_number + "\nexpected: " + s2
                            + "\nactual: " + s;
                    line_number++;
                }
                br.close();
                br2.close();
            } catch (IOException err) {
                err.printStackTrace();
                return;
            }
        }
        assert --i != 0 : "No test cases found!";
        System.out.println("You passed all " + i + " test case" + (i == 1 ? "" : "s") + "! (" + (mx / msec) + "ms)");
    }

}
