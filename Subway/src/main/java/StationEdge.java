public class StationEdge implements Edge<Station, StationWeight> {
    private final Station to;
    private final StationWeight weight;

    public StationEdge(Station to, int transferCount, int weight) {
        this.to = to;
        this.weight = new StationWeight(transferCount, weight);
    }

    public Station getTo() {
        return to;
    }

    public StationWeight getWeight() {
        return weight;
    }
}
