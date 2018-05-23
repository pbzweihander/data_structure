import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class LinkedList<T extends Comparable<T>> implements Comparable<LinkedList<T>> {
    private class Node {
        public T value;
        public Node next;

        public Node(T value) {
            this.value = value;
        }
    }

    private Node root;
    private Node tail;

    public LinkedList() {
        root = null;
        tail = null;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void add(T element) {
        if (tail == null) {
            root = new Node(element);
            tail = root;
        } else {
            tail.next = new Node(element);
            tail = tail.next;
        }
    }

    public int compareTo(LinkedList<T> other) {
        Node myNode = root;
        Node othersNode = other.root;
        while (myNode != null && othersNode != null) {
            int compare = myNode.value.compareTo(othersNode.value);
            if (compare != 0)
                return compare;
        }
        int out = 0;
        if (myNode == null)
            out--;
        if (othersNode == null)
            out++;
        return out;
    }

    public <U> List<U> map(Function<? super T, U> mapper) {
        List<U> list = new ArrayList<>();
        for (Node node = root; node != null; node = node.next)
            list.add(mapper.apply(node.value));
        return list;
    }

    public void forEach(Consumer<? super T> consumer) {
        for (Node node = root; node != null; node = node.next)
            consumer.accept(node.value);
    }

    public T find(Predicate<? super T> predicate) {
        for (Node node = root; node != null; node = node.next)
            if (predicate.test(node.value))
                return node.value;
        return null;
    }

    public T find(T value) {
        return find(v -> v.compareTo(value) == 0);
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>();
        for (Node node = root; node != null; node = node.next)
            list.add(node.value);
        return list;
    }
}
