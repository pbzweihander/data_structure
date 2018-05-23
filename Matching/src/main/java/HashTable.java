import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public class HashTable<K extends Comparable<K>, V extends Comparable<V>> {
    private final Function<K, Integer> hasher;
    private List<AvlTree<Pair<K, LinkedList<V>>>> array;

    public HashTable(Function<K, Integer> hasher, int size) {
        array = new ArrayList<>(Collections.nCopies(size, null));
        this.hasher = hasher;
    }

    public void add(K key, V value) {
        int keyHash = hasher.apply(key);

        AvlTree<Pair<K, LinkedList<V>>> tree = array.get(keyHash);

        if (tree != null) {
            Pair<K, LinkedList<V>> listPair = tree.find(p -> p.first().compareTo(key));

            if (listPair != null)
                listPair.second().add(value);
            else {
                LinkedList<V> newList = new LinkedList<>();
                newList.add(value);
                tree.insert(new Pair<>(key, newList));
            }
        } else {
            tree = new AvlTree<>();
            LinkedList<V> list = new LinkedList<>();
            list.add(value);
            tree.insert(new Pair<>(key, list));
            array.set(keyHash, tree);
        }
    }

    public boolean containsKey(K key) {
        AvlTree<Pair<K, LinkedList<V>>> tree = array.get(hasher.apply(key));
        return tree != null && tree.find(p -> p.first().compareTo(key)) != null;
    }

    public List<V> get(K key) {
        return array.get(hasher.apply(key)).find(p -> p.first().compareTo(key)).second().toList();
    }

    public List<K> keysWithHash(int hash) {
        AvlTree<Pair<K, LinkedList<V>>> tree = array.get(hash);
        if (tree == null)
            return new ArrayList<>();
        return tree.preorder(p -> p.first());
    }
}
