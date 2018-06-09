public interface Edge<V extends Vertex<?, W>, W extends Weight> {
    public V getTo();

    public W getWeight();
}
