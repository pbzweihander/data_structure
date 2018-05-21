public abstract class ISort {
    protected int[] arr;
    protected int n;

    public ISort() {
        this.arr = null;
        n = 0;
    }

    public ISort(int[] arr) {
        this.arr = arr;
        n = arr.length;
    }

    public void with(int[] arr) {
        this.arr = arr;
        n = arr.length;
    }

    public abstract int[] sort();
}
