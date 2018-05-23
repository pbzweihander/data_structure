public class TestUtil {
    public static <T> String joinArray(T[] arr, String delimiter) {
        StringBuilder builder = new StringBuilder();
        for (T t : arr) {
            builder.append(t);
            builder.append(delimiter);
        }
        builder.setLength(builder.length() - delimiter.length());
        return builder.toString();
    }
}
