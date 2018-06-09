import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Subway {
    private static final Pattern inputPattern = Pattern.compile("^(\\S+?) (\\S+?)( !)?$");
    private static final Pattern stationPattern = Pattern.compile("^(\\S+?) (\\S+?) (\\S+?)$");
    private static final Pattern edgePattern = Pattern.compile("^(\\S+?) (\\S+?) (\\d+?)$");

    private HashMap<String, List<Station>> nameToStationsMap;
    private HashMap<String, Station> codeToStationMap;

    public Subway(Reader reader) throws IOException {
        nameToStationsMap = new HashMap<>();
        codeToStationMap = new HashMap<>();

        BufferedReader bufferedReader = new BufferedReader(reader);

        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null && !inputLine.isEmpty()) {
            Matcher matcher = stationPattern.matcher(inputLine);

            String code = matcher.group(1);
            String name = matcher.group(2);
            String line = matcher.group(3);

            if (nameToStationsMap.containsKey(name)) {
                Station station = new Station(code, name, line);
                codeToStationMap.put(code, station);
                List<Station> stations = nameToStationsMap.get(name);
                for (Station s : stations) {
                    s.addEdge(station, 1, 0);
                    station.addEdge(s, 1, 0);
                }
            } else {
                Station station = new Station(code, name, line);
                List<Station> stations = new ArrayList<>();
                stations.add(station);
                nameToStationsMap.put(name, stations);
                codeToStationMap.put(code, station);
            }
        }

        while ((inputLine = bufferedReader.readLine()) != null) {
            Matcher matcher = edgePattern.matcher(inputLine);

            String from = matcher.group(1);
            String to = matcher.group(2);
            Integer weight = Integer.getInteger(matcher.group(3));

            Station fromStation = codeToStationMap.get(from);
            Station toStation = codeToStationMap.get(to);
            fromStation.addEdge(toStation, 0, weight);
        }
    }

    public Route getShortestRoute(String start, String end) {
        return null;
    }

    public Route getMinimumTransiRoute(String start, String end) {
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File file = new File(args[1]);
        Subway subway = new Subway(new FileReader(file));

        BufferedReader stdInReader = new BufferedReader(new InputStreamReader(System.in));

        String line;
        while (((line = stdInReader.readLine()) != null) && !line.equals("QUIT"))
            processLine(subway, line);
    }

    public static void processLine(Subway subway, String line) {
        Matcher matcher = inputPattern.matcher(line);

        if (matcher.matches()) {
            String station1 = matcher.group(1);
            String station2 = matcher.group(2);
            String group3 = matcher.group(3);

            List<Station> route;
            if (group3 != null && !group3.isEmpty())
                route = subway.getMinimumTransiRoute(station1, station2);
            else
                route = subway.getShortestRoute(station1, station2);

            System.out.println(route.toString());
        }
    }
}
