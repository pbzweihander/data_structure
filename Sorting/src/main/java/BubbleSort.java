public class BubbleSort extends ISwapSort {
    public BubbleSort() {
        super();
    }

    public BubbleSort(int[] arr) {
        super(arr);
    }

    public int[] sort() {
        boolean swapped = true;
        while (swapped) {
            swapped = false;
            for (int i = 1; i < n; i++) {
                if (arr[i - 1] > arr[i]) {
                    swap(i - 1, i);
                    swapped = true;
                }
            }
            n--;
        }

        return arr;
    }
}
