public class HeapSort extends ISwapSort {
    public HeapSort() {
        super();
    }

    public HeapSort(int[] arr) {
        super(arr);
    }

    private static int indexOfParentOf(int i) {
        return (i - 1) / 2;
    }

    private static int indexOfLeftChildOf(int i) {
        return 2 * i + 1;
    }

    public int[] sort() {
        heapify();

        int end = n - 1;
        while (end > 0) {
            swap(end, 0);
            end--;
            siftDown(0, end);
        }

        return arr;
    }

    private void heapify() {
        for (int i = indexOfParentOf(n - 1); i >= 0; i--)
            siftDown(i, n - 1);
    }

    private void siftDown(int start, int end) {
        int root = start;

        while (indexOfLeftChildOf(root) <= end) {
            int child = indexOfLeftChildOf(root);
            int swap = root;

            if (arr[swap] < arr[child])
                swap = child;
            if (child + 1 <= end && arr[swap] < arr[child + 1])
                swap = child + 1;
            if (swap == root)
                return;
            else {
                swap(root, swap);
                root = swap;
            }
        }
    }
}
