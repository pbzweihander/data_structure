import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;
import java.util.Random;

import org.junit.Test;

public class SortTest {
    private class Data {
        public int[] orig;
        public int[] sorted;

        public Data(int count) {
            Random random = new Random();
            orig = random.ints().limit(count).toArray();
            sorted = (int[]) orig.clone();
            Arrays.sort(sorted);
        }
    }

    private static final int N = 1900;

    @Test
    public void testBubble() {
        Data data = new Data(N);
        BubbleSort sorter = new BubbleSort(data.orig);
        int[] sorted = sorter.sort();
        assertArrayEquals(data.sorted, sorted);
    }

    @Test
    public void testInsertion() {
        Data data = new Data(N);
        InsertionSort sorter = new InsertionSort(data.orig);
        int[] sorted = sorter.sort();
        assertArrayEquals(data.sorted, sorted);
    }

    @Test
    public void testHeap() {
        Data data = new Data(N);
        HeapSort sorter = new HeapSort(data.orig);
        int[] sorted = sorter.sort();
        assertArrayEquals(data.sorted, sorted);
    }

    @Test
    public void testMerge() {
        Data data = new Data(N);
        MergeSort sorter = new MergeSort(data.orig);
        int[] sorted = sorter.sort();
        assertArrayEquals(data.sorted, sorted);
    }

    @Test
    public void testQuick() {
        Data data = new Data(N);
        QuickSort sorter = new QuickSort(data.orig);
        int[] sorted = sorter.sort();
        assertArrayEquals(data.sorted, sorted);
    }

    @Test
    public void testRadix() {
        Data data = new Data(N);
        RadixSort sorter = new RadixSort(data.orig);
        int[] sorted = sorter.sort();
        assertArrayEquals(data.sorted, sorted);
    }
}
