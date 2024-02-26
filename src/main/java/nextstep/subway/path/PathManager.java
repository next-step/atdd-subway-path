package nextstep.subway.path;

import java.util.HashMap;
import java.util.List;
import nextstep.subway.line.Line;
import nextstep.subway.section.Section;
import nextstep.subway.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathManager {
    public static WeightedMultigraph<Station, DefaultWeightedEdge> makeGraph(HashMap<Line, List<Station>> map) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        for (Line line : map.keySet()) {
            List<Station> stations = map.get(line);
            addVertex(graph, stations);
            addEdge(graph, line.getSectionList());
        }

        return graph;
    }

    private static void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Station> stations) {
        stations.stream()
                .filter(station -> !graph.containsVertex(station))
                .forEach(graph::addVertex);
    }

    private static void addEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Section> sections) {
        sections.stream()
                .filter(section -> !graph.containsEdge(section.getUpStation(), section.getDownStation()))
                .forEach(section -> {
                    Station upStation = section.getUpStation();
                    Station downStation = section.getDownStation();
                    DefaultWeightedEdge defaultWeightedEdge = graph.addEdge(upStation,
                        downStation);
                    Long distance = section.getDistance();
                    graph.setEdgeWeight(defaultWeightedEdge, distance);
                });
    }

    public static void validStartAndEndStation(Station startStation, Station endStation) {
        if(startStation.getId().equals(endStation.getId())) {
            throw new IllegalArgumentException("출발역과 도착역이 같은 경로는 조회할 수 없습니다.");
        }
    }


    public static void validConnectedStation(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station startStation, Station endStation) {
        GraphPath<Station, DefaultWeightedEdge> edges = DijkstraShortestPath.findPathBetween(graph, startStation, endStation);
        if(edges == null) {
            throw new IllegalArgumentException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }
}
