import java.util.List;

public class Route {
    private final List<Station> stations;
    private final StationWeight weight;

    public Route(List<Station> stations, StationWeight weight) {
        this.stations = stations;
        this.weight = weight;
    }

    public Route(Pair<List<Station>, StationWeight> pair) {
        this(pair.first(), pair.second());
    }

    public long getTime() {
        return weight.getTime();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < stations.size(); i++) {
            Station s = stations.get(i);
            if (i + 1 < stations.size() && stations.get(i + 1).getName().equals(s.getName())) {
                builder.append("[" + s.getName() + "] ");
                i++;
            } else
                builder.append(s.getName() + " ");
        }
        return builder.toString().trim();
    }
}
