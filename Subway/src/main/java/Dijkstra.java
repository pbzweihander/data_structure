import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Dijkstra<V extends Vertex<E, W>, E extends Edge<? extends Vertex<?, W>, W>, W extends Weight> {
    private final List<V> graph;
    private final List<W> weightList;
    private final HashMap<V, Integer> vertexToIndexMap;

    private final Class<W> classOfW;

    private Comparator<W> comparator;
    private Queue<Pair<Integer, W>> unvisited;

    private W getInstanceOfW() {
        try {
            return classOfW.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Dijkstra(List<V> list, Class<W> classOfW) {
        this.classOfW = classOfW;
        graph = new ArrayList<>(list);
        vertexToIndexMap = new HashMap<>();
        weightList = new ArrayList<>();
        graph.add(0, null);
        int i = 1;
        for (V vertex : list) {
            graph.add(vertex);
            vertexToIndexMap.put(vertex, i);
            i++;
        }
        unvisited = new PriorityQueue<>(graph.size(), (a, b) -> a.second().compareTo(b.second()));
        comparator = null;
    }

    public Dijkstra(List<V> list, Class<W> classOfW, Comparator<W> comparator) {
        this(list, classOfW);
        this.comparator = comparator;
        unvisited = new PriorityQueue<>(graph.size(), (a, b) -> comparator.compare(a.second(), b.second()));
    }

    public void setComparator(Comparator<W> comparator) {
        this.comparator = comparator;
        if (comparator != null)
            unvisited = new PriorityQueue<>(graph.size(), (a, b) -> comparator.compare(a.second(), b.second()));
        else
            unvisited = new PriorityQueue<>(graph.size(), (a, b) -> a.second().compareTo(b.second()));
    }

    public Pair<List<V>, W> findShortestPath(V start, V end) {
        List<V> starts = new ArrayList<>();
        List<V> ends = new ArrayList<>();
        starts.add(start);
        ends.add(end);
        return findShortestPath(starts, ends);
    }

    public Pair<List<V>, W> findShortestPath(List<V> starts, List<V> ends) {
        weightList.clear();
        unvisited.clear();
        for (int i = 0; i < graph.size(); i++)
            weightList.add(getInstanceOfW());

        Set<Integer> startSet = new HashSet<>();
        Set<Integer> endSet = new HashSet<>();
        for (V v : starts)
            startSet.add(vertexToIndexMap.get(v));
        for (V v : ends)
            endSet.add(vertexToIndexMap.get(v));

        for (V vertex : starts) {
            int index = vertexToIndexMap.get(vertex);
            W weight = weightList.get(index);
            weight.setZero();
            unvisited.add(new Pair<>(index, weight));
        }

        int[] backtracker = new int[graph.size()];
        boolean[] visited = new boolean[graph.size()];

        Pair<Integer, W> startPair = unvisited.poll();

        int now = startPair.first();
        W weightSum = startPair.second();
        while (!endSet.contains(now)) {
            V nowVertex = graph.get(now);

            for (E edge : nowVertex) {
                Object toVertex = edge.getTo();
                int to = vertexToIndexMap.get(toVertex);

                if (visited[to])
                    continue;

                W weight = edge.getWeight();

                W toWeight = weightList.get(to);
                W addedWeight = getInstanceOfW();
                addedWeight.setZero();
                addedWeight.add(weightSum);
                addedWeight.add(weight);

                int compare;
                if (comparator == null)
                    compare = toWeight.compareTo(addedWeight);
                else
                    compare = comparator.compare(toWeight, addedWeight);

                if (compare > 0) {
                    toWeight.setZero();
                    toWeight.add(addedWeight);
                    backtracker[to] = now;
                }

                unvisited.add(new Pair<>(to, toWeight));
            }
            visited[now] = true;

            int nextIndex = now;
            while (visited[nextIndex]) {
                Pair<Integer, W> pair = unvisited.poll();
                nextIndex = pair.first();
                weightSum = pair.second();
            }
            now = nextIndex;
        }

        List<V> route = new LinkedList<>();

        while (!startSet.contains(now)) {
            route.add(0, graph.get(now));
            now = backtracker[now];
        }
        route.add(0, graph.get(now));

        return new Pair<>(route, weightSum);
    }
}
