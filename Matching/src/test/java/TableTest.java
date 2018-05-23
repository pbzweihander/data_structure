import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class TableTest {
    public static int hash(String s) {
        return s.chars().sum() % 100;
    }

    public static String collectBytes(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (byte b : bytes)
            builder.append((char) b);
        return builder.toString();
    }

    @Test
    public void randomTest() {
        Random random = new Random();
        HashTable<String, Integer> table = new HashTable<>(TableTest::hash, 100);

        final int N = 100000;
        final int K = 10;

        List<Pair<String, Integer>> orig = new ArrayList<>();
        random.ints(N).forEach(i -> {
            byte[] bytes = new byte[K];
            random.nextBytes(bytes);
            orig.add(new Pair<>(collectBytes(bytes), i));
        });

        for (Pair<String, Integer> pair : orig)
            table.add(pair.first(), pair.second());

        for (Pair<String, Integer> pair : orig)
            assert table.get(pair.first()).contains(pair.second());
    }
}
