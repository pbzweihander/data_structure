
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MyLinkedList<T extends Comparable<T>> implements ListInterface<T> {
    // dummy head
    Node<T> head;
    int numItems;

    public MyLinkedList() {
        head = new Node<T>(null);
    }

    /**
     * {@code Iterable<T>}를 구현하여 iterator() 메소드를 제공하는 클래스의 인스턴스는
     * 다음과 같은 자바 for-each 문법의 혜택을 볼 수 있다.
     *
     * <pre>
     *  for (T item: iterable) {
     *  	item.someMethod();
     *  }
     * </pre>
     *
     * @see PrintCmd#apply(MovieDB)
     * @see SearchCmd#apply(MovieDB)
     * @see java.lang.Iterable#iterator()
     */
    public final Iterator<T> iterator() {
        return new MyLinkedListIterator<T>(this);
    }

    @Override
    public boolean isEmpty() {
        return head.getNext() == null;
    }

    @Override
    public int size() {
        int i = 0;
        for (Node<T> node = head; node.getNext() != null; node = node.getNext())
            i++;
        return i;
    }

    @Override
    public T first() {
        return head.getNext().getItem();
    }

    /**
     * Add a item to the list. This method assures sorted and distinct list.
     */
    @Override
    public void add(T item) {
        Node<T> curr = head;
        while (curr.getNext() != null && curr.getNext().getItem().compareTo(item) < 0) {
            if (curr.getNext().getItem().compareTo(item) == 0)
                return;
            curr = curr.getNext();
        }
        curr.insertNext(item);
    }

    @Override
    public void removeAll() {
        head.setNext(null);
    }

    /**
     * Make stream of this list
     *
     * @see MovieDB#search(String)
     * @return stream of this list
     */
    public Stream<T> stream() {
        return StreamSupport.stream(Spliterators.spliterator(iterator(), size(), 0), false);
    }

    /**
     * Add all items of other list. Use this method for collection.
     *
     * @see MovieDB#search(String)
     */
    public void addAll(MyLinkedList<T> other) {
        other.forEach(item -> add(item));
    }
}

class MyLinkedListIterator<T extends Comparable<T>> implements Iterator<T> {
    // Implement the iterator for MyLinkedList.
    // You have to maintain the current position of the iterator.
    private MyLinkedList<T> list;
    private Node<T> curr;
    private Node<T> prev;

    public MyLinkedListIterator(MyLinkedList<T> list) {
        this.list = list;
        this.curr = list.head;
        this.prev = null;
    }

    @Override
    public boolean hasNext() {
        return curr.getNext() != null;
    }

    @Override
    public T next() {
        if (!hasNext())
            throw new NoSuchElementException();

        prev = curr;
        curr = curr.getNext();

        return curr.getItem();
    }

    @Override
    public void remove() {
        if (prev == null)
            throw new IllegalStateException("next() should be called first");
        if (curr == null)
            throw new NoSuchElementException();
        prev.removeNext();
        list.numItems -= 1;
        curr = prev;
        prev = null;
    }
}
