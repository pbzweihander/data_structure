public class QuickSort extends ISwapSort {
    public QuickSort() {
        super();
    }

    public QuickSort(int[] arr) {
        super(arr);
    }

    private void quickSort(int low, int high) {
        if (low < high) {
            int p = partition(low, high);
            quickSort(low, p - 1);
            quickSort(p + 1, high);
        }
    }

    private int partition(int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++)
            if (arr[j] < pivot)
                swap(++i, j);
        swap(i + 1, high);
        return i + 1;
    }

    public int[] sort() {
        quickSort(0, n - 1);

        return arr;
    }
}
