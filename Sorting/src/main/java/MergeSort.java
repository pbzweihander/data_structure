public class MergeSort extends ISort {
    private int[] workingArray;

    public MergeSort() {
        super();
    }

    public MergeSort(int[] arr) {
        super(arr);
        workingArray = (int[]) arr.clone();
    }

    @Override
    public void with(int[] arr) {
        super.with(arr);
        workingArray = (int[]) arr.clone();
    }

    private void splitAndMerge(int[] from, int[] to, int start, int end) {
        if (end - start < 2)
            return;

        int middle = (start + end) / 2;
        splitAndMerge(to, from, start, middle);
        splitAndMerge(to, from, middle, end);

        merge(from, to, start, middle, end);
    }

    private void merge(int[] from, int[] to, int start, int middle, int end) {
        int index_of_left = start;
        int index_of_right = middle;

        for (int i = start; i < end; i++) {
            if (index_of_left < middle && (index_of_right >= end || from[index_of_left] <= from[index_of_right]))
                to[i] = from[index_of_left++];
            else
                to[i] = from[index_of_right++];
        }
    }

    public int[] sort() {
        splitAndMerge(workingArray, arr, 0, n);

        return arr;
    }
}
