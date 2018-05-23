import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class AvlTree<T extends Comparable<T>> {
    private class Node {
        public Node left;
        public Node right;
        public int height;
        public T value;

        public Node(T value) {
            this.value = value;
            left = null;
            right = null;
            height = 1;
        }
    }

    private Node root;

    public AvlTree() {
        root = null;
    }

    private int max(int a, int b) {
        return b > a ? b : a;
    }

    private int heightOf(Node node) {
        return node == null ? 0 : node.height;
    }

    private int calculateHeight(Node node) {
        return max(heightOf(node.left), heightOf(node.right)) + 1;
    }

    private Node attachTo(Node node, T value) {
        if (node == null)
            return new Node(value);

        int compare = node.value.compareTo(value);
        if (compare > 0)
            node.left = attachTo(node.left, value);
        else
            node.right = attachTo(node.right, value);

        int heightOfLeft = heightOf(node.left);
        int heightOfRight = heightOf(node.right);

        node.height = calculateHeight(node);

        int balance = heightOfLeft - heightOfRight;

        // LL case
        if (balance > 1 && value.compareTo(node.left.value) < 0)
            return rightRotate(node);
        // RR case
        if (balance < -1 && value.compareTo(node.right.value) > 0)
            return leftRotate(node);
        // LR case
        if (balance > 1 && value.compareTo(node.left.value) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }
        // RL case
        if (balance < -1 && value.compareTo(node.right.value) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    private Node rightRotate(Node node) {
        Node left = node.left;
        Node rightOfLeft = left.right;

        left.right = node;
        node.left = rightOfLeft;

        node.height = calculateHeight(node);
        left.height = calculateHeight(left);

        return left;
    }

    private Node leftRotate(Node node) {
        Node right = node.right;
        Node rightOfLeft = right.left;

        right.left = node;
        node.right = rightOfLeft;

        node.height = calculateHeight(node);
        right.height = calculateHeight(right);

        return right;
    }

    public void insert(T element) {
        root = attachTo(root, element);
    }

    private T findInner(Node node, Function<? super T, Integer> comparator) {
        if (node == null)
            return null;
        int compare = comparator.apply(node.value);
        if (compare == 0)
            return node.value;
        else if (compare > 0)
            return findInner(node.left, comparator);
        else
            return findInner(node.right, comparator);
    }

    public T find(Function<? super T, Integer> comparator) {
        return findInner(root, comparator);
    }

    public T find(T value) {
        return findInner(root, v -> v.compareTo(value));
    }

    private <U> void preorderInner(List<U> list, Node node, Function<? super T, U> mapper) {
        if (node == null)
            return;
        list.add(mapper.apply(node.value));
        preorderInner(list, node.left, mapper);
        preorderInner(list, node.right, mapper);
    }

    public <U> List<U> preorder(Function<? super T, U> mapper) {
        List<U> list = new ArrayList<>();
        preorderInner(list, root, mapper);
        return list;
    }

    public List<T> preorder() {
        return preorder(t -> t);
    }
}
