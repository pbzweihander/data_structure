public class TestUtil {
    public static <T, U> void assertArrayEquals(T[] expecteds, U[] actuals) {
        try {
            org.junit.Assert.assertArrayEquals(expecteds, actuals);
        } catch (AssertionError e) {
            StringBuilder expected = new StringBuilder();
            StringBuilder actual = new StringBuilder();

            expected.append("expecteds: ( ");
            for (T t : expecteds)
                expected.append(t.toString() + ", ");
            expected.append(")");

            actual.append("actuals: ( ");
            for (U u : actuals)
                actual.append(u.toString() + ", ");
            actual.append(")");

            throw new AssertionError("\n" + expected.toString() + "\n" + actual.toString() + "\n", e);
        }
    }
}
