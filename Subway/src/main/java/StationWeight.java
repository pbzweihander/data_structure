public class StationWeight implements Weight {
    private long transferCount;
    private long weight;
    private boolean isInfinity;

    public StationWeight() {
        transferCount = 0;
        weight = 0;
        isInfinity = true;
    }

    public StationWeight(int transferCount, int weight) {
        this.transferCount = transferCount;
        this.weight = weight;
        isInfinity = false;
    }

    public int compareTo(Weight other) {
        if (other instanceof StationWeight)
            return compareTo((StationWeight) other);
        else
            throw new ClassCastException();
    }

    public int compareTo(StationWeight other) {
        if (isInfinity && other.isInfinity)
            return 0;
        else if (isInfinity)
            return 1;
        else if (other.isInfinity)
            return -1;
        else if (transferCount == other.transferCount)
            return (int) (weight - other.weight);
        else
            return (int) (transferCount - other.transferCount);
    }

    public void add(Weight other) {
        if (other instanceof StationWeight) {
            StationWeight otherAsSelf = (StationWeight) other;
            transferCount += otherAsSelf.transferCount;
            weight += otherAsSelf.weight;
            isInfinity = false;
        } else
            throw new ClassCastException();
    }

    public boolean isInfinity() {
        return isInfinity;
    }

    public void setZero() {
        transferCount = 0;
        weight = 0;
        isInfinity = false;
    }
}
