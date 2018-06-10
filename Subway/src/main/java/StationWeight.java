public class StationWeight implements Weight {
    private long transferCount;
    private long time;
    private boolean isInfinity;

    public StationWeight() {
        transferCount = 0;
        time = 0;
        isInfinity = true;
    }

    public StationWeight(int transferCount, int weight) {
        this.transferCount = transferCount;
        this.time = weight;
        isInfinity = false;
    }

    public StationWeight(StationWeight other) {
        transferCount = other.transferCount;
        time = other.time;
        isInfinity = other.isInfinity;
    }

    public long getTransferCount() {
        return transferCount;
    }

    public long getTime() {
        return time;
    }

    public int compareTo(Weight other) {
        if (other instanceof StationWeight)
            return compareTo((StationWeight) other);
        else
            throw new ClassCastException();
    }

    public int compareTo(StationWeight other) {
        if (isInfinity() && other.isInfinity())
            return 0;
        else if (isInfinity())
            return 1;
        else if (other.isInfinity())
            return -1;
        else if (getTime() == other.getTime())
            return (int) (getTransferCount() - other.getTransferCount());
        else
            return (int) (getTime() - other.getTime());
    }

    public void add(Weight other) {
        if (other instanceof StationWeight) {
            StationWeight otherAsSelf = (StationWeight) other;
            transferCount += otherAsSelf.transferCount;
            time += otherAsSelf.time;
            isInfinity = false;
        } else
            throw new ClassCastException();
    }

    public boolean isInfinity() {
        return isInfinity;
    }

    public void setZero() {
        transferCount = 0;
        time = 0;
        isInfinity = false;
    }

    @Override
    public String toString() {
        return transferCount + "-" + time;
    }
}
