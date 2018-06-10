public class StationEdge implements Edge<Station, StationWeight> {
    private final Station to;
    private final StationWeight weight;

    public StationEdge(Station to, int transferCount, int time) {
        this.to = to;
        weight = new StationWeight(transferCount, time);
    }

    public Station getTo() {
        return to;
    }

    public StationWeight getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "-> " + to.getName() + " (" + weight.toString() + ")";
    }
}
