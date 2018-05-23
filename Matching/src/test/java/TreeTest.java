import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

public class TreeTest {
    private class Tree<T extends Comparable<T>> {
        public class Node {
            public T value;
            public Node left;
            public Node right;
            public int height;

            public Node(T value) {
                this.value = value;
                height = 1;
                left = null;
                right = null;
            }
        }

        public Node root;

        public Tree(List<T> preorder) {
            root = constructTree(preorder);
        }

        private Node constructTree(List<T> preorder) {
            int lengthOfPreorder = preorder.size();

            if (lengthOfPreorder == 0)
                return null;

            Node node = new Node(preorder.get(0));

            int firstBiggerThanParent = 1;
            for (; firstBiggerThanParent < lengthOfPreorder
                    && preorder.get(firstBiggerThanParent).compareTo(node.value) <= 0; firstBiggerThanParent++)
                ;

            node.left = constructTree(preorder.subList(1, firstBiggerThanParent));
            node.right = constructTree(preorder.subList(firstBiggerThanParent, lengthOfPreorder));

            node.height = calculateHeight(node);

            return node;
        }

        private void preorderInner(List<T> list, Node node) {
            if (node == null)
                return;
            list.add(node.value);
            preorderInner(list, node.left);
            preorderInner(list, node.right);
        }

        public List<T> preorder() {
            List<T> list = new ArrayList<>();
            preorderInner(list, root);
            return list;
        }
    }

    private static int max(int a, int b) {
        return b > a ? b : a;
    }

    private static int heightOf(Tree<?>.Node node) {
        return node == null ? 0 : node.height;
    }

    private static int calculateHeight(Tree<?>.Node node) {
        return max(heightOf(node.left), heightOf(node.right)) + 1;
    }

    private static int diff(int a, int b) {
        return max(a - b, b - a);
    }

    @Test
    public void LLBalance() {
        AvlTree<Integer> tree = new AvlTree<>();

        tree.insert(3);
        tree.insert(2);
        tree.insert(1);

        Integer[] expected = new Integer[] { 2, 1, 3 };
        Integer[] actual = tree.preorder().toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(expected, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", expected, actual);
    }

    @Test
    public void RRBalance() {
        AvlTree<Integer> tree = new AvlTree<>();

        tree.insert(1);
        tree.insert(2);
        tree.insert(3);

        Integer[] expected = new Integer[] { 2, 1, 3 };
        Integer[] actual = tree.preorder().toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(expected, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", expected, actual);
    }

    @Test
    public void LRBalance() {
        AvlTree<Integer> tree = new AvlTree<>();

        tree.insert(3);
        tree.insert(1);
        tree.insert(2);

        Integer[] expected = new Integer[] { 2, 1, 3 };
        Integer[] actual = tree.preorder().toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(expected, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", expected, actual);
    }

    @Test
    public void RLBalance() {
        AvlTree<Integer> tree = new AvlTree<>();

        tree.insert(1);
        tree.insert(3);
        tree.insert(2);

        Integer[] expected = new Integer[] { 2, 1, 3 };
        Integer[] actual = tree.preorder().toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(expected, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", expected, actual);
    }

    @Test
    public void findTest() {
        AvlTree<Integer> tree = new AvlTree<>();

        assert tree.find(1) == null;
        assert tree.find(2) == null;
        assert tree.find(3) == null;
        assert tree.find(4) == null;
        assert tree.find(5) == null;

        tree.insert(1);
        tree.insert(2);
        tree.insert(3);

        assert tree.find(1) != null;
        assert tree.find(2) != null;
        assert tree.find(3) != null;
        assert tree.find(4) == null;
        assert tree.find(5) == null;
    }

    @Test
    public void randomTest() {
        Random random = new Random();
        AvlTree<Integer> avlTree = new AvlTree<>();
        int[] orig = random.ints(100000).toArray();

        for (int i : orig)
            avlTree.insert(i);

        for (int i : orig)
            assert avlTree.find(i) != null;
        for (int i : orig)
            assert avlTree.find(v -> v.compareTo(i)) != null;

        List<Integer> list = avlTree.preorder();

        Tree<Integer> testTree = new Tree<>(list);

        Integer[] expected = avlTree.preorder().toArray(new Integer[0]);
        Integer[] actual = testTree.preorder().toArray(new Integer[0]);
        assertArrayEquals("expected: (" + TestUtil.joinArray(expected, ", ") + "), actual: ("
                + TestUtil.joinArray(actual, ", ") + ")", expected, actual);

        nodeHeightTest(testTree.root);
    }

    private void nodeHeightTest(Tree<?>.Node node) {
        if (node == null)
            return;
        int heightOfLeft = heightOf(node.left);
        int heightOfRight = heightOf(node.right);
        assert diff(heightOfLeft, heightOfRight) <= 1 : "height of left: " + heightOfLeft + ", right: " + heightOfRight;
        nodeHeightTest(node.left);
        nodeHeightTest(node.right);
    }
}
