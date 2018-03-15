import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class BigInteger {
    private class Vector {
        private int[] innerArr;
        public int size;

        public Vector() {
            innerArr = new int[2];
            size = 0;
        }

        public void append(int b) {
            if (innerArr.length <= size)
                expand();
            innerArr[size] = b;
            size++;
        }

        public int get(int i) {
            return innerArr[i];
        }

        public void clear() {
            size = 0;
        }

        public void shift(int n, int replacement) {
            while (innerArr.length < size + n)
                expand();
            for (int i = size - 1; i >= 0; i--)
                innerArr[i + n] = innerArr[i];
            for (int i = 0; i < n; i++)
                innerArr[i] = replacement;
            size += n;
        }

        public void trim(int toBeTrimed) {
            while (innerArr[size - 1] == toBeTrimed)
                size--;
        }

        private void expand() {
            int[] newArr = new int[innerArr.length * 2];
            for (int i = 0; i < innerArr.length; i++)
                newArr[i] = innerArr[i];
            innerArr = null;
            innerArr = newArr;
        }
    }

    private static final String QUIT_COMMAND = "quit";
    private static final String MSG_INVALID_INPUT = "입력이 잘못되었습니다.";

    private Vector innerVector;
    private boolean isPlus = true;

    // implement this
    public static final Pattern EXPRESSION_PATTERN = Pattern
            .compile("^\\s*([+-]?)\\s*(\\d+)\\s*([+\\-*])\\s*([+-]?)\\s*(\\d+)\\s*$");

    private BigInteger() {
        innerVector = new Vector();
        innerVector.append(0);
    }

    public BigInteger(String s) {
        innerVector = new Vector();
        s = "0000" + s;

        for (int i = s.length(); i >= 4; i -= 4) {
            String substring = s.substring(i - 4, i);
            int digit = Integer.parseInt(substring);
            innerVector.append(digit);
        }
    }

    public static BigInteger add(BigInteger a, BigInteger b) {
        if (a.isPlus) {
            if (b.isPlus)
                return addUnsigned(a, b);
            else
                return subtractUnsigned(a, b);
        } else {
            if (b.isPlus)
                return subtractUnsigned(b, a);
            else {
                BigInteger res = addUnsigned(a, b);
                res.applySign('-');
                return res;
            }
        }
    }

    public static BigInteger subtract(BigInteger a, BigInteger b) {
        if (a.isPlus) {
            if (b.isPlus)
                return subtractUnsigned(a, b);
            else
                return addUnsigned(a, b);
        } else {
            if (b.isPlus) {
                BigInteger res = addUnsigned(a, b);
                res.applySign('-');
                return res;
            } else
                return subtractUnsigned(b, a);
        }
    }

    private static BigInteger addUnsigned(BigInteger a, BigInteger b) {
        BigInteger res = new BigInteger();
        res.innerVector.clear();

        int aSize = a.innerVector.size;
        int bSize = b.innerVector.size;
        BigInteger shorterBI = aSize < bSize ? a : b;
        int minSize = shorterBI.innerVector.size;
        int maxSize = aSize > bSize ? aSize : bSize;

        for (int i = minSize; i < maxSize; i++)
            shorterBI.innerVector.append(0);

        int carry = 0;
        for (int i = 0; i < maxSize; i++) {
            int digit = a.innerVector.get(i) + b.innerVector.get(i) + carry;
            carry = digit / 10000;
            res.innerVector.append(digit % 10000);
        }
        res.innerVector.append(carry);

        return res;
    }

    private static BigInteger subtractUnsigned(BigInteger a, BigInteger b) {
        BigInteger res = new BigInteger();
        res.innerVector.clear();

        if (BigInteger.compareToUnsigned(a, b) < 0) {
            res = BigInteger.subtractUnsigned(b, a);
            res.isPlus = false;
            return res;
        }

        int aSize = a.innerVector.size;
        int bSize = b.innerVector.size;
        BigInteger shorterBI = aSize < bSize ? a : b;
        int minSize = shorterBI.innerVector.size;
        int maxSize = aSize > bSize ? aSize : bSize;

        for (int i = minSize; i < maxSize; i++)
            shorterBI.innerVector.append(0);

        int carry = 0;
        for (int i = 0; i < maxSize; i++) {
            int digit = a.innerVector.get(i) - b.innerVector.get(i) - carry;
            carry = digit < 0 ? 1 : 0;
            res.innerVector.append((digit + 10000) % 10000);
        }

        return res;
    }

    public static BigInteger multiply(BigInteger a, BigInteger b) {
        BigInteger res = new BigInteger();

        for (int i = 0; i < b.innerVector.size; i++) {
            BigInteger m = BigInteger.multiply(a, b.innerVector.get(i));
            m.innerVector.shift(i, 0);
            res = BigInteger.addUnsigned(res, m);
        }
        res.isPlus = a.isPlus == b.isPlus;

        return res;
    }

    public static BigInteger multiply(BigInteger a, int b) {
        BigInteger res = new BigInteger();
        res.innerVector.clear();

        int carry = 0;
        for (int i = 0; i < a.innerVector.size; i++) {
            int digit = a.innerVector.get(i) * b + carry;
            carry = digit / 10000;
            res.innerVector.append(digit % 10000);
        }
        res.innerVector.append(carry);

        return res;
    }

    public BigInteger add(BigInteger other) {
        return BigInteger.add(this, other);
    }

    public BigInteger subtract(BigInteger other) {
        return BigInteger.subtract(this, other);
    }

    public BigInteger multiply(BigInteger other) {
        return BigInteger.multiply(this, other);
    }

    @Override
    public String toString() {
        return (isPlus ? "" : "-") + IntStream.of(innerVector.innerArr).mapToObj(i -> ("0000" + i))
                .map(s -> s.substring(s.length() - 4, s.length())).reduce((a, b) -> b + a).map(s -> s.chars()
                        .dropWhile(c -> c == '0').mapToObj(c -> "" + (char) c).reduce((a, b) -> a + b).orElse("0"))
                .orElse("0");
    }

    public static int compareToUnsigned(BigInteger a, BigInteger b) {
        a.innerVector.trim(0);
        b.innerVector.trim(0);
        if (a.innerVector.size != b.innerVector.size)
            return a.innerVector.size - b.innerVector.size;
        int i = a.innerVector.size - 1;
        while (a.innerVector.get(i) == b.innerVector.get(i) && i > 0)
            i--;
        return a.innerVector.get(i) - b.innerVector.get(i);
    }

    public void applySign(char sign) {
        if (sign == '+') {
            isPlus = true;
        } else if (sign == '-') {
            isPlus = false;
        }
    }

    public static BigInteger evaluate(String input) throws IllegalArgumentException {
        Matcher matcher = EXPRESSION_PATTERN.matcher(input);
        if (!matcher.matches())
            throw new IllegalArgumentException(input);

        String s1 = matcher.group(1);
        String s2 = matcher.group(4);
        BigInteger num1 = new BigInteger(matcher.group(2));
        BigInteger num2 = new BigInteger(matcher.group(5));
        String operator = matcher.group(3);

        if (!s1.isEmpty())
            num1.applySign(s1.charAt(0));
        if (!s2.isEmpty())
            num2.applySign(s2.charAt(0));

        BigInteger result;

        switch (operator) {
        case "+":
            result = num1.add(num2);
            break;
        case "-":
            result = num1.subtract(num2);
            break;
        case "*":
            result = num1.multiply(num2);
            break;
        default:
            throw new IllegalArgumentException();
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        try (InputStreamReader isr = new InputStreamReader(System.in)) {
            try (BufferedReader reader = new BufferedReader(isr)) {
                boolean done = false;
                while (!done) {
                    String input = reader.readLine();

                    try {
                        done = processInput(input);
                    } catch (IllegalArgumentException e) {
                        System.err.println(MSG_INVALID_INPUT);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
            }
        }
    }

    private static boolean processInput(String input) throws IllegalArgumentException {
        boolean quit = isQuitCmd(input);

        if (quit) {
            return true;
        } else {
            BigInteger result = evaluate(input);
            System.out.println(result.toString());

            return false;
        }
    }

    private static boolean isQuitCmd(String input) {
        return input.equalsIgnoreCase(QUIT_COMMAND);
    }
}
