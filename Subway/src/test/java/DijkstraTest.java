import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class DijkstraTest {
    public static class SimpleWeight implements Weight {
        private int weight;
        private boolean isInfinity;

        public SimpleWeight() {
            weight = 0;
            isInfinity = true;
        }

        public SimpleWeight(int weight) {
            this.weight = weight;
            isInfinity = false;
        }

        public SimpleWeight(SimpleWeight other) {
            weight = other.weight;
            isInfinity = other.isInfinity;
        }

        public void add(Weight other) {
            if (other instanceof SimpleWeight) {
                weight += ((SimpleWeight) other).weight;
                isInfinity = false;
            } else
                throw new ClassCastException();
        }

        public boolean isInfinity() {
            return isInfinity;
        }

        public int getWeight() {
            return weight;
        }

        public void setZero() {
            weight = 0;
            isInfinity = false;
        }

        public int compareTo(SimpleWeight other) {
            if (isInfinity && other.isInfinity)
                return 0;
            else if (isInfinity)
                return 1;
            else if (other.isInfinity)
                return -1;
            else
                return weight - other.weight;
        }

        public int compareTo(Weight other) {
            if (other instanceof SimpleWeight)
                return compareTo((SimpleWeight) other);
            else
                throw new ClassCastException();
        }

        @Override
        public String toString() {
            return isInfinity ? "Inf" : ("" + weight);
        }
    }

    public static class SimpleEdge implements Edge<SimpleVertex, SimpleWeight> {
        private final SimpleVertex to;
        private final SimpleWeight weight;

        public SimpleEdge(SimpleVertex to, int weight) {
            this.to = to;
            this.weight = new SimpleWeight(weight);
        }

        public SimpleVertex getTo() {
            return to;
        }

        public SimpleWeight getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "-> " + to.toString() + " (" + weight.toString() + ")";
        }
    }

    @SuppressWarnings("serial")
    public static class SimpleVertex extends LinkedList<SimpleEdge> implements Vertex<SimpleEdge, SimpleWeight> {
        private String name;

        public SimpleVertex(String name) {
            super();
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void addEdge(SimpleVertex to, int weight) {
            add(new SimpleEdge(to, weight));
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Test
    public void simpleTest() {
        SimpleVertex S = new SimpleVertex("S");
        SimpleVertex B = new SimpleVertex("B");
        SimpleVertex C = new SimpleVertex("C");
        SimpleVertex D = new SimpleVertex("D");

        S.addEdge(B, 24);
        B.addEdge(S, 24);
        S.addEdge(D, 20);
        D.addEdge(S, 20);
        S.addEdge(C, 3);
        C.addEdge(S, 3);
        C.addEdge(D, 12);
        D.addEdge(C, 12);

        List<SimpleVertex> list = new ArrayList<>();
        list.add(S);
        list.add(B);
        list.add(C);
        list.add(D);

        Dijkstra<SimpleVertex, SimpleEdge, SimpleWeight> dijkstra = new Dijkstra<>(list, SimpleWeight.class);

        Pair<List<SimpleVertex>, SimpleWeight> toB = dijkstra.findShortestPath(S, B);
        TestUtil.assertArrayEquals(new SimpleVertex[] { S, B }, toB.first().toArray());
        assertEquals(24, toB.second().getWeight());

        Pair<List<SimpleVertex>, SimpleWeight> toC = dijkstra.findShortestPath(S, C);
        TestUtil.assertArrayEquals(new SimpleVertex[] { S, C }, toC.first().toArray());
        assertEquals(3, toC.second().getWeight());

        Pair<List<SimpleVertex>, SimpleWeight> toD = dijkstra.findShortestPath(S, D);
        TestUtil.assertArrayEquals(new SimpleVertex[] { S, C, D }, toD.first().toArray());
        assertEquals(15, toD.second().getWeight());
    }
}
