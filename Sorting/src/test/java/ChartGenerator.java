import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

            System.out.println(getName() + " thread end");
        }

        public XYSeries getResult() {
            return series;
        }
    }

    @Test
    public void generateChart() throws IOException, InterruptedException {
        XYSeriesCollection datasetOfBubble = new XYSeriesCollection();
        XYSeriesCollection datasetOfInsertion = new XYSeriesCollection();
        XYSeriesCollection datasetOfRadix = new XYSeriesCollection();
        XYSeriesCollection datasetOfOther = new XYSeriesCollection();
        int[] arrayOfNForBubble = generateArrayOfN(1000, 10000, 100);
        int[] arrayOfNForInsertion = generateArrayOfN(1000, 20000, 200);
        int[] arrayOfNForRadix = generateArrayOfN(1000, 80000, 800);
        int[] arrayOfNForOther = generateArrayOfN(100000, 1000000, 10000);

        ArrayList<ResultingThread<XYSeries>> threads = new ArrayList<>();

        threads.add(new SortSeriesGenerater<BubbleSort>(BubbleSort.class, "Bubble", arrayOfNForBubble));
        threads.add(new SortSeriesGenerater<InsertionSort>(InsertionSort.class, "Insertion", arrayOfNForInsertion));
        threads.add(new SortSeriesGenerater<RadixSort>(RadixSort.class, "Radix", arrayOfNForRadix));
        threads.add(new SortSeriesGenerater<HeapSort>(HeapSort.class, "Heap", arrayOfNForOther));
        threads.add(new SortSeriesGenerater<MergeSort>(MergeSort.class, "Merge", arrayOfNForOther));
        threads.add(new SortSeriesGenerater<QuickSort>(QuickSort.class, "Quick", arrayOfNForOther));

        for (Thread t : threads)
            t.start();
        for (Thread t : threads)
            t.join();

        datasetOfBubble.addSeries(threads.remove(0).getResult());
        datasetOfInsertion.addSeries(threads.remove(0).getResult());
        datasetOfRadix.addSeries(threads.remove(0).getResult());
        for (ResultingThread<XYSeries> t : threads)
            datasetOfOther.addSeries(t.getResult());

        JFreeChart chartOfBubble = ChartFactory.createScatterPlot("Bubble Sort", "N", "Time", datasetOfBubble,
                PlotOrientation.VERTICAL, false, false, false);
        JFreeChart chartOfInsertion = ChartFactory.createScatterPlot("Insertion Sort", "N", "Time", datasetOfInsertion,
                PlotOrientation.VERTICAL, false, false, false);
        JFreeChart chartOfRadix = ChartFactory.createScatterPlot("Radix Sort", "N", "Time", datasetOfRadix,
                PlotOrientation.VERTICAL, false, false, false);
        JFreeChart chartOfOther = ChartFactory.createScatterPlot("Other Sorts", "N", "Time", datasetOfOther,
                PlotOrientation.VERTICAL, true, false, false);

        File fileForBubble = new File("chart_bubble.png");
        File fileForInsertion = new File("chart_insertion.png");
        File fileForRadix = new File("chart_radix.png");
        File fileForOther = new File("chart_other.png");
        ChartUtilities.saveChartAsPNG(fileForBubble, chartOfBubble, 800, 600);
        ChartUtilities.saveChartAsPNG(fileForInsertion, chartOfInsertion, 800, 600);
        ChartUtilities.saveChartAsPNG(fileForRadix, chartOfRadix, 800, 600);
        ChartUtilities.saveChartAsPNG(fileForOther, chartOfOther, 800, 600);
    }
}
