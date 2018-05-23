import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;

public class Matching {
    private static final Pattern INPUT_PATTERN = Pattern.compile("^(.) (.+)$");
    private static final int K = 6;

    private HashTable<String, Pair<Integer, Integer>> table;
    private List<String> origStrings;

    public Matching() {
        clear();
    }

    private static int hash(String s) {
        return s.chars().sum() % 100;
    }

    public void clear() {
        table = new HashTable<>(Matching::hash, 100);
        origStrings = new ArrayList<>();
    }

    public void insertLines(String[] lines) {
        for (int lineNumber = 1; lineNumber <= lines.length; lineNumber++) {
            String line = lines[lineNumber - 1];
            origStrings.add(line);

            for (int startIndex = 1; startIndex <= line.length() - K + 1; startIndex++) {
                String substring = line.substring(startIndex - 1, startIndex + K - 1);
                Pair<Integer, Integer> pairToInsert = new Pair<>(lineNumber, startIndex);

                table.add(substring, pairToInsert);
            }
        }
    }

    public List<String> indexTable(int index) {
        return table.keysWithHash(index);
    }

    public List<Pair<Integer, Integer>> search(String pattern) {
        String substring = pattern.substring(0, K);
        int lengthOfPattern = pattern.length();

        List<Pair<Integer, Integer>> outList = new ArrayList<>();

        if (!table.containsKey(substring))
            return outList;

        List<Pair<Integer, Integer>> pairList = table.get(substring);

        pairList.forEach(p -> {
            int lineNumber = p.first() - 1;
            int startIndex = p.second() - 1;
            String line = origStrings.get(lineNumber);
            if (line.length() >= startIndex + lengthOfPattern)
                if (line.substring(startIndex, startIndex + lengthOfPattern).compareTo(pattern) == 0)
                    outList.add(p);
        });

        return outList;
    }

    public static void main(String args[]) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        Matching matching = new Matching();

        try {
            for (String line = bufferedReader.readLine(); line.compareTo("QUIT") != 0; line = bufferedReader
                    .readLine()) {
                try {
                    processLine(matching, line);
                } catch (IOException e) {
                    System.out.println("입력이 잘못되었습니다. 오류 : " + e.toString());
                }
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    protected static void processLine(Matching matching, String input) throws IOException {
        if (input.isEmpty())
            throw new IOException("Empty input");

        Matcher matcher = INPUT_PATTERN.matcher(input);

        if (!matcher.matches())
            throw new IOException("Mismatched input pattern: " + input);

        String operator = matcher.group(1);
        String operand = matcher.group(2);

        switch (operator) {
        case "<":
            inputData(matching, operand);
            break;
        case "@":
            int index;
            try {
                index = Integer.parseInt(operand);
            } catch (NumberFormatException e) {
                throw new IOException("Illegal argument: NumberFormatException: " + operand);
            }
            printData(matching, index);
            break;
        case "?":
            searchPattern(matching, operand);
            break;
        default:
            throw new IOException("Illegal operation: " + operator);
        }
    }

    private static void inputData(Matching matching, String filename) throws IOException {
        File file = new File(filename);
        FileReader reader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(reader);

        matching.clear();
        matching.insertLines(bufferedReader.lines().toArray(String[]::new));
        bufferedReader.close();
        reader.close();
    }

    private static void printData(Matching matching, int index) {
        List<String> list = matching.indexTable(index);
        if (list.isEmpty())
            System.out.println("EMPTY");
        else
            System.out.println(list.stream().collect(Collectors.joining(" ")));
    }

    private static void searchPattern(Matching matching, String pattern) {
        List<Pair<Integer, Integer>> list = matching.search(pattern);
        if (list.isEmpty())
            System.out.println("(0, 0)");
        else
            System.out.println(list.stream().map(p -> p.toString()).collect(Collectors.joining(" ")));
    }
}
