public class RadixSort extends ISort {
    public RadixSort() {
        super();
    }

    public RadixSort(int[] arr) {
        super(arr);
    }

    public int[] sort() {
        int[] lows = new int[n];
        int[] highs = new int[n];

        for (int radix = 0; radix < 32; radix++) {
            int index_of_lows = 0;
            int index_of_highs = 0;

            for (int number : arr) {
                if ((number & (1 << radix)) == 0)
                    lows[index_of_lows++] = number;
                else
                    highs[index_of_highs++] = number;
            }
            for (int i = 0; i < n; i++)
                if (radix < 31) {
                    if (i < index_of_lows)
                        arr[i] = lows[i];
                    else
                        arr[i] = highs[i - index_of_lows];
                } else {
                    if (i < index_of_highs)
                        arr[i] = highs[i];
                    else
                        arr[i] = lows[i - index_of_highs];
                }
        }

        return arr;
    }
}
