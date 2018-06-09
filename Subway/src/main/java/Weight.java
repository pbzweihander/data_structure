public interface Weight extends Comparable<Weight> {
    public void add(Weight other);

    public boolean isInfinity();

    public void setInfinity(boolean inf);
}
