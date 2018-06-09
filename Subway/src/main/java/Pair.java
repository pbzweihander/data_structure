public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }

    public T first() {
        return first;
    }

    public U second() {
        return second;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        else if (other instanceof Pair<?, ?>) {
            Pair<?, ?> otherPair = (Pair<?, ?>) other;
            return first.equals(otherPair.first) && second.equals(otherPair.second);
        } else
            return false;
    }
}
