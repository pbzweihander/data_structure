import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class Dijkstra<V extends Vertex<E, W>, E extends Edge<? extends Vertex<?, W>, W>, W extends Weight> {
    private final List<V> graph;
    private final List<W> weightList;
    private final Queue<Pair<Integer, W>> unvisited;
    private final HashMap<V, Integer> vertexToIndexMap;

    private final Class<W> classOfW;

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
    }

    private W doDijkstraAndReturnWeightSum(int[] backtracker, int until) {
        boolean[] visited = new boolean[graph.size()];

        Pair<Integer, W> startPair = unvisited.poll();
        int now = startPair.first();
        W weightSum = startPair.second();
        while (until != now) {
            V nowVertex = graph.get(now);
            for (E edge : nowVertex) {
                W weight = edge.getWeight();
                Object toVertex = edge.getTo();
                int to = vertexToIndexMap.get(toVertex);

                W toWeight = weightList.get(to);
                toWeight.add(weightSum);
                toWeight.add(weight);

                unvisited.add(new Pair<>(to, toWeight));
            }
            visited[now] = true;

            int nextIndex = now;
            while (visited[nextIndex]) {
                Pair<Integer, W> pair = unvisited.poll();
                nextIndex = pair.first();
                weightSum = pair.second();
            }
            backtracker[nextIndex] = now;
            now = nextIndex;
        }

        return weightSum;
    }

    public Pair<List<V>, W> findShortestPath(V start, V end) {
        weightList.clear();
        unvisited.clear();
        weightList.addAll(Collections.nCopies(graph.size(), getInstanceOfW()));

        int[] backtracker = new int[graph.size()];

        int startIndex = vertexToIndexMap.get(start);
        int endIndex = vertexToIndexMap.get(end);
        W startWeight = weightList.get(startIndex);

        startWeight.setInfinity(false);
        unvisited.add(new Pair<>(startIndex, startWeight));

        W weightSum = doDijkstraAndReturnWeightSum(backtracker, endIndex);

        List<V> route = new LinkedList<>();

        int nowIndex = endIndex;
        while (startIndex != nowIndex) {
            route.add(0, graph.get(nowIndex));
            nowIndex = backtracker[nowIndex];
        }

        return new Pair<>(route, weightSum);
    }

    public Pair<List<V>, W> findShortedPathWithMultipleStart(List<V> starts, V end) {
        weightList.clear();
        unvisited.clear();
        weightList.addAll(Collections.nCopies(graph.size(), getInstanceOfW()));

        int[] backtracker = new int[graph.size()];

        int endIndex = vertexToIndexMap.get(end);

        for (V vertex : starts) {
            int index = vertexToIndexMap.get(vertex);
            W weight = weightList.get(index);
            weight.setInfinity(false);
            unvisited.add(new Pair<>(index, weight));
        }

        W weightSum = doDijkstraAndReturnWeightSum(backtracker, endIndex);

        List<V> route = new LinkedList<>();

        int nowIndex = endIndex;
        while (starts.contains(graph.get(nowIndex))) {
            route.add(0, graph.get(nowIndex));
            nowIndex = backtracker[nowIndex];
        }

        return new Pair<>(route, weightSum);
    }
}
