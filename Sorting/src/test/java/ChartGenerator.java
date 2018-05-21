import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class ChartGenerator {
    private static final Random RANDOM = new Random();
    private static final int REPEAT = 50;

    private static HashMap<String, Integer> progressMap = new HashMap<>();
    private static HashMap<String, Integer> countMap = new HashMap<>();

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

    private static void printProgress() {
        countMap.entrySet().forEach(entry -> {
            String n = entry.getKey();
            int p = progressMap.get(n);
            int c = entry.getValue();
            System.out.println(n + " " + p + "/" + c + " " + (p * 100 / c) + "%");
        });
        System.out.println();
    }

    private class XYSeriesCollectionWithName {
        public XYSeriesCollection collection;
        public String name;

        public XYSeriesCollectionWithName(String name, XYSeriesCollection collection) {
            this.name = name;
            this.collection = collection;
        }
    }

    private abstract class ResultingThread<T> extends Thread {
        public abstract T getResult();
    }

    private class SortThread<T extends ISort> extends Thread {
        private int n;
        private Class<T> sorterType;
        private String sortName;
        private long time;

        public SortThread(Class<T> sorterType, String sortName, String threadName, int n) {
            this.sorterType = sorterType;
            this.sortName = sortName;
            setName(threadName);
            this.n = n;

            countMap.replace(sortName, countMap.get(sortName) + 1);
        }

        public void run() {
            // System.out.println(getName() + " thread start");
            printProgress();

            long t;
            int[] data = generateRandomCases(n);
            try {
                ISort sorter = sorterType.newInstance();
                sorter.with(data);

                t = System.nanoTime();
                sorter.sort();
                time = System.nanoTime() - t;
            } catch (InstantiationException e) {
                e.printStackTrace(System.err);
            } catch (IllegalAccessException e) {
                e.printStackTrace(System.err);
            }

            progressMap.replace(sortName, progressMap.get(sortName) + 1);
            // System.out.println(getName() + " thread end with time " + time);
            printProgress();
        }

        public long getResult() {
            return time;
        }
    }

    private class RepeatSortThread<T extends ISort> extends Thread {
        private int n;
        private long time;
        private ArrayList<SortThread<T>> threads;

        public RepeatSortThread(Class<T> sorterType, String sortName, String threadName, int n, int repeat) {
            setName(threadName);
            this.n = n;

            threads = new ArrayList<>();
            for (int i = 0; i < repeat; i++) {
                SortThread<T> t = new SortThread<T>(sorterType, sortName, getName() + " " + i, n);
                threads.add(t);
            }
        }

        public void run() {
            // System.out.println(getName() + " thread start");

            for (Thread t : threads) {
                t.start();
            }

            ArrayList<Long> times = new ArrayList<>();
            for (SortThread<T> t : threads) {
                try {
                    t.join();
                    times.add(t.getResult());
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }
            times.sort(Long::compareTo);
            time = times.get(times.size() / 2);

            // System.out.println(getName() + " thread end with time " + time);
        }

        public long getResult() {
            return time;
        }

        public int getN() {
            return n;
        }
    }

    private class SortSeriesGenerater<T extends ISort> extends ResultingThread<XYSeries> {
        private ArrayList<RepeatSortThread<T>> threads;
        private XYSeries series;

        public SortSeriesGenerater(Class<T> sorterType, String name, int[] arrayOfN) {
            setName(name);
            threads = new ArrayList<>();
            series = new XYSeries(getName());

            progressMap.put(name, 0);
            countMap.put(name, 0);

            for (int n : arrayOfN) {
                RepeatSortThread<T> t = new RepeatSortThread<>(sorterType, getName(), getName() + " " + n, n, REPEAT);
                threads.add(t);
            }
        }

        public void run() {
            // System.out.println(getName() + " thread start");

            for (Thread t : threads)
                t.start();

            for (Thread t : threads) {
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            }

            ArrayList<XYDataItem> items = new ArrayList<>();
            for (RepeatSortThread<T> t : threads)
                items.add(new XYDataItem(t.getN(), t.getResult()));

            List<XYDataItem> subList = items.subList(items.size() - 5, items.size());
            long sum = 0;
            for (XYDataItem item : subList)
                sum += item.getY().longValue();
            final long average = sum / 5;

            items.removeIf(item -> (item.getY().longValue() > average));

            for (XYDataItem item : items)
                series.add(item);

            // System.out.println(getName() + " thread end");
        }

        public XYSeries getResult() {
            return series;
        }
    }

    @Test
    public void generateChart() throws IOException, InterruptedException {
        int[] arrayOfNForSlow = generateArrayOfN(1000, 20000, 200);
        int[] arrayOfNForFast = generateArrayOfN(1000, 50000, 500);

        ArrayList<ResultingThread<XYSeries>> threads = new ArrayList<>();

        threads.add(new SortSeriesGenerater<BubbleSort>(BubbleSort.class, "Bubble", arrayOfNForSlow));
        threads.add(new SortSeriesGenerater<InsertionSort>(InsertionSort.class, "Insertion", arrayOfNForSlow));
        threads.add(new SortSeriesGenerater<RadixSort>(RadixSort.class, "Radix", arrayOfNForFast));
        threads.add(new SortSeriesGenerater<HeapSort>(HeapSort.class, "Heap", arrayOfNForFast));
        threads.add(new SortSeriesGenerater<MergeSort>(MergeSort.class, "Merge", arrayOfNForFast));
        threads.add(new SortSeriesGenerater<QuickSort>(QuickSort.class, "Quick", arrayOfNForFast));

        for (Thread t : threads) {
            t.start();
            t.join();

        }
        // for (Thread t : threads)

        if (Files.notExists(Paths.get("chart")))
            Files.createDirectories(Paths.get("chart"));

        for (ResultingThread<XYSeries> t : threads) {
            XYSeriesCollection collection = new XYSeriesCollection();
            collection.addSeries(t.getResult());
            JFreeChart chart = ChartFactory.createScatterPlot(t.getName() + " Sort", "N", "Time (nanoseonds)",
                    collection, PlotOrientation.VERTICAL, false, false, false);
            File file = new File("chart/" + t.getName() + ".png");
            ChartUtilities.saveChartAsPNG(file, chart, 800, 600);
        }
    }
}
