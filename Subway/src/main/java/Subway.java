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

    private final HashMap<String, List<Station>> nameToStationsMap;
    private final HashMap<String, Station> codeToStationMap;
    private final List<Station> stations;
    private final Dijkstra<Station, StationEdge, StationWeight> dijkstra;

    public Subway(Reader reader) throws IOException {
        nameToStationsMap = new HashMap<>();
        codeToStationMap = new HashMap<>();
        stations = new ArrayList<>();

        BufferedReader bufferedReader = new BufferedReader(reader);

        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null && !inputLine.isEmpty()) {
            Matcher matcher = stationPattern.matcher(inputLine);

            if (!matcher.matches())
                continue;

            String code = matcher.group(1);
            String name = matcher.group(2);
            String line = matcher.group(3);

            Station station = new Station(code, name, line);
            codeToStationMap.put(code, station);
            if (nameToStationsMap.containsKey(name)) {
                List<Station> list = nameToStationsMap.get(name);
                for (Station s : list) {
                    s.addEdge(station, 1, 5);
                    station.addEdge(s, 1, 5);
                }
                list.add(station);
            } else {
                List<Station> list = new ArrayList<>();
                list.add(station);
                nameToStationsMap.put(name, list);
            }
            stations.add(station);
        }

        while ((inputLine = bufferedReader.readLine()) != null) {
            Matcher matcher = edgePattern.matcher(inputLine);

            if (!matcher.matches())
                continue;

            String from = matcher.group(1);
            String to = matcher.group(2);
            Integer weight = Integer.parseInt(matcher.group(3));

            Station fromStation = codeToStationMap.get(from);
            Station toStation = codeToStationMap.get(to);
            fromStation.addEdge(toStation, 0, weight);
        }
        dijkstra = new Dijkstra<>(stations, StationWeight.class);
    }

    public Route getShortestRoute(String startName, String endName) {
        List<Station> starts = nameToStationsMap.get(startName);
        List<Station> ends = nameToStationsMap.get(endName);

        return new Route(dijkstra.findShortestPath(starts, ends));
    }

    public Route getMinimumTransiRoute(String startName, String endName) {
        List<Station> starts = nameToStationsMap.get(startName);
        List<Station> ends = nameToStationsMap.get(endName);

        return new Route(dijkstra.findShortestPath(starts, ends, (a, b) -> a.compareWithTransfer(b)));
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        File file = new File(args[0]);
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

            Route route;
            if (group3 != null && !group3.isEmpty())
                route = subway.getMinimumTransiRoute(station1, station2);
            else
                route = subway.getShortestRoute(station1, station2);

            System.out.println(route.toString());
            System.out.println(route.getTime());
        }
    }
}
