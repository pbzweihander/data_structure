import java.util.List;

public interface Route extends List<Station> {
    public static String toStringOf(Route r) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < r.size(); i++) {
            Station s = r.get(i);
            if (i + 1 < r.size() && r.get(i + 1).getName().equals(s.getName())) {
                builder.append("[" + s.getName() + "] ");
                i++;
            } else
                builder.append(s.getName() + " ");
        }
        return builder.toString().trim();
    }
}
