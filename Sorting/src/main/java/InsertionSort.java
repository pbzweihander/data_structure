public class InsertionSort extends ISort {
    public InsertionSort() {
        super();
    }

    public InsertionSort(int[] arr) {
        super(arr);
    }

    public int[] sort() {
        int i = 1;
        while (i < n) {
            int x = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] > x) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = x;
            i++;
        }
        return arr;
    }
}
