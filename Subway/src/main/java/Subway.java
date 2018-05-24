import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subway {
    public Subway(Reader reader) {
        BufferedReader bufferedReader = new BufferedReader(reader);
    }

    public Route getShortestRoute(String start, String end) {
        return new Route();
    }

    public Route getMinimumTransiRoute(String start, String end) {
        return new Route();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File file = new File(args[1]);
        Subway subway = new Subway(new FileReader(file));

        BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while (((line = stdInReader.readLine()) != null) && !line.equals("QUIT"))
            processLine(subway, line);
    }

    private static final Pattern inputPattern = Pattern.compile("^(\\S+?) (\\S+?)( !)?$");

    public static void processLine(Subway subway, String line) {
        Matcher matcher = inputPattern.matcher(line);

        if (matcher.matches()) {
            String station1 = matcher.group(1);
            String station2 = matcher.group(2);
            String group3 = matcher.group(3);

            Route route;
            if (group3 != null && !group3.isEmpty())
                route = subway.getMinimumTransiRoute(station1, station2);
            else
                route = subway.getShortestRoute(station1, station2);

            System.out.println(route.toString());
        }
    }
}
