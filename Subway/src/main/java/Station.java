import java.util.LinkedList;

@SuppressWarnings("serial")
public class Station extends LinkedList<StationEdge> implements Vertex<StationEdge, StationWeight> {
    private final String code;
    private final String name;
    private final String line;

    private Integer hashCode;

    public Station(String code, String name, String line) {
        super();
        this.code = code;
        this.name = name;
        this.line = line;
        hashCode = null;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLine() {
        return line;
    }

    public void addEdge(Station to, int transferCount, int weight) {
        add(new StationEdge(to, transferCount, weight));
    }

    @Override
    public int hashCode() {
        if (hashCode == null)
            hashCode = super.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        return code + " " + name + " " + super.toString();
    }
}
