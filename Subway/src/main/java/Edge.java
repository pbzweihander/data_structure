public interface Edge<V extends Vertex<?, W>, W extends Weight> extends Comparable<Edge<V, W>> {
    public V getTo();

    public W getWeight();
}
