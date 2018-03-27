/**
 * Genre, Title 을 관리하는 영화 데이터베이스.
 *
 * MyLinkedList 를 사용해 각각 Genre와 Title에 따라 내부적으로 정렬된 상태를
 * 유지하는 데이터베이스이다.
 */
public class MovieDB {
    private MyLinkedList<MovieDBItem> innerList;

    public MovieDB() {
        innerList = new MyLinkedList<>();
    }

    /**
     * Insert the given item to the MovieDB.
     *
     * @param item the item to insert
     */
    public void insert(MovieDBItem item) {
        innerList.add(item);
    }

    /**
     * Remove the given item from the MovieDB.
     *
     * @param item the item to remove
     */
    public void delete(MovieDBItem item) {
        for (Node<MovieDBItem> curr = innerList.head; curr.getNext() != null; curr = curr.getNext()) {
            if (curr.getNext().getItem().compareTo(item) == 0) {
                curr.removeNext();
                break;
            }
        }
    }

    /**
     * Search the given term from the MovieDB.
     *
     * @param term substring to search in title
     * @return list of items that contains given term in title
     */
    public MyLinkedList<MovieDBItem> search(String term) {
        return innerList.stream().filter(item -> item.getTitle().contains(term)).collect(MyLinkedList<MovieDBItem>::new,
                MyLinkedList<MovieDBItem>::add, (left, right) -> left.addAll(right));
    }

    /**
     * Returns all items of the MovieDatabase.
     *
     * @return list of all items of this database
     */
    public MyLinkedList<MovieDBItem> items() {
        return innerList;
    }
}
