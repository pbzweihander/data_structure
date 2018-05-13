import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartGenerator {
    private static final Random RANDOM = new Random();
    private static final int FROM_N = 1000;
    private static final int TO_N = 10000;
    private static final int STEP = 100;
    private static final int REPEAT = 20;

    private static int[] generateRandomCases(int n) {
        return RANDOM.ints(n).toArray();
    }

    private static int[] generateArrayOfN(int from, int to, int step) {
        int length = (to - from) / step;
        int[] arr = new int[length];

        int j = from;
        for (int i = 0; i < length; i++) {
            arr[i] = j;
            j += step;
        }

        return arr;
    }

    private abstract class ResultingThread<T> extends Thread {
        public abstract T getResult();
    }

    private class SortThread<T extends ISort> extends Thread {
        private int n;
        private Class<T> sorterType;
        private long time;

        public SortThread(Class<T> sorterType, String name, int n) {
            this.sorterType = sorterType;
            setName(name);
            this.n = n;
        }

        public void run() {
            System.out.println(getName() + " thread start");

            long t;
            int[] data = generateRandomCases(n);
            try {
                ISort sorter = sorterType.newInstance();
                sorter.with(data);

                t = System.currentTimeMillis();
                sorter.sort();
                time = System.currentTimeMillis() - t;
            } catch (InstantiationException e) {
                e.printStackTrace(System.err);
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.err);
            }

            System.out.println(getName() + " thread end with time " + time);
        }

        public long getResult() {
            return time;
        }
    }

    private class RepeatSortThread<T extends ISort> extends Thread {
        private int n;
        private Class<T> sorterType;
        private int repeat;
        private long time;

        public RepeatSortThread(Class<T> sorterType, String name, int n, int repeat) {
            this.sorterType = sorterType;
            setName(name);
            this.repeat = repeat;
            this.n = n;
        }

        public void run() {
            System.out.println(getName() + " thread start");
            ArrayList<SortThread<T>> threads = new ArrayList<>();

            for (int i = 0; i < repeat; i++) {
                SortThread<T> t = new SortThread<T>(sorterType, getName() + " " + i, n);
                threads.add(t);
                t.start();
            }

            time = 0;
            for (SortThread<T> t : threads) {
                try {
                    t.join();
                    time += t.getResult();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
            time /= repeat;

            System.out.println(getName() + " thread end with time " + time);
        }

        public long getResult() {
            return time;
        }

        public int getN() {
            return n;
        }
    }

    private class SortSeriesGenerater<T extends ISort> extends ResultingThread<XYSeries> {
        private Class<T> sorterType;
        private int[] arrayOfN;
        private ArrayList<RepeatSortThread<T>> threads;
        private XYSeries series;

        public SortSeriesGenerater(Class<T> sorterType, String name, int[] arrayOfN) {
            this.sorterType = sorterType;
            setName(name);
            this.arrayOfN = arrayOfN;
            threads = new ArrayList<>();
            series = new XYSeries(getName());
        }

        public void run() {
            System.out.println(getName() + " thread start");

            for (int n : arrayOfN) {
                RepeatSortThread<T> t = new RepeatSortThread<>(sorterType, getName() + " " + n, n, REPEAT);
                threads.add(t);
                t.start();
            }
            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
            for (RepeatSortThread<T> t : threads)
                series.add(t.getN(), t.getResult());

            System.out.println(getName() + " thread end");
        }

        public XYSeries getResult() {
            return series;
        }
    }

    private XYDataset generateDataset() throws InterruptedException {
        XYSeriesCollection dataset = new XYSeriesCollection();
        int[] arrayOfN = generateArrayOfN(FROM_N, TO_N, STEP);

        ArrayList<ResultingThread<XYSeries>> threads = new ArrayList<>();

        threads.add(new SortSeriesGenerater<BubbleSort>(BubbleSort.class, "Bubble", arrayOfN));
        threads.add(new SortSeriesGenerater<InsertionSort>(InsertionSort.class, "Insertion", arrayOfN));
        threads.add(new SortSeriesGenerater<HeapSort>(HeapSort.class, "Heap", arrayOfN));
        threads.add(new SortSeriesGenerater<MergeSort>(MergeSort.class, "Merge", arrayOfN));
        threads.add(new SortSeriesGenerater<QuickSort>(QuickSort.class, "Quick", arrayOfN));
        threads.add(new SortSeriesGenerater<RadixSort>(RadixSort.class, "Radix", arrayOfN));

        for (Thread t : threads)
            t.start();
        for (Thread t : threads)
            t.join();
        for (ResultingThread<XYSeries> t : threads)
            dataset.addSeries(t.getResult());

        return dataset;
    }

    @Test
    public void generateChart() throws IOException, InterruptedException {
        XYDataset dataset = generateDataset();
        JFreeChart chart = ChartFactory.createScatterPlot("SortingTest", "N", "Time", dataset, PlotOrientation.VERTICAL,
                true, false, false);

        File file = new File("chart.png");
        ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
    }
}
