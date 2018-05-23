import static org.junit.Assert.assertArrayEquals;

import java.util.List;
import java.util.Random;

import org.junit.Test;

public class ListTest {
    @Test
    public void findTest() {
        LinkedList<Integer> list = new LinkedList<>();

        assert list.find(1) == null;
        assert list.find(2) == null;
        assert list.find(3) == null;
        assert list.find(4) == null;
        assert list.find(5) == null;

        list.add(1);
        list.add(2);
        list.add(3);

        assert list.find(1) != null;
        assert list.find(2) != null;
        assert list.find(3) != null;
        assert list.find(4) == null;
        assert list.find(5) == null;
    }

    @Test
    public void mapTest() {
        LinkedList<Integer> list = new LinkedList<>();

        list.add(1);
        list.add(2);
        list.add(3);

        List<Integer> newList = list.map(i -> i * 2);

        Integer[] expected = new Integer[] { 2, 4, 6 };
        Integer[] actual = newList.toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(expected, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", expected, actual);
    }

    @Test
    public void randomTest() {
        Random random = new Random();
        LinkedList<Integer> list = new LinkedList<>();
        Integer[] orig = random.ints(10000).boxed().toArray(Integer[]::new);

        for (int i : orig)
            list.add(i);

        for (int i : orig)
            assert list.find(i) != null;
        for (int i : orig)
            assert list.find(v -> v.compareTo(i) == 0) != null;

        Integer[] actual = list.toList().toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(orig, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", orig, actual);
    }
}
