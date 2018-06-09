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
        else {
            if (isInfinity) {
                if (other.isInfinity())
                    return 0;
                else
                    return 1;
            } else {
                if (other.isInfinity())
                    return -1;
                else
                    return 0;
            }
        }
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
            throw new NullPointerException();
    }

    public boolean isInfinity() {
        return isInfinity;
    }

    public void setInfinity(boolean inf) {
        isInfinity = inf;
    }
}
