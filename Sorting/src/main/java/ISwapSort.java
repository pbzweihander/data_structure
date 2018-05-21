public abstract class ISwapSort extends ISort {
    public ISwapSort() {
        super();
    }

    public ISwapSort(int[] arr) {
        super(arr);
    }

    protected void swap(int i, int j) {
        int tmp = arr[i];
        arr[i] = arr[j];
        arr[j] = tmp;
    }
}
