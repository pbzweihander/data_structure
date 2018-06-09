import java.util.Collection;

public interface Vertex<E extends Edge<? extends Vertex<?, W>, W>, W extends Weight> extends Collection<E> {

}
