package subway.path;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import subway.section.Section;
import subway.station.Station;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathGenerator {
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathGenerator(List<Section> sections) {
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        init(sections);
    }

    private void init(List<Section> sections) {
        for (Station station : toStations(sections)) {
            if (!graph.containsVertex(station)) {
                graph.addVertex(station);
            }
        }
        for (Section section : sections) {
            if (!graph.containsEdge(section.getUpStation(), section.getDownStation())) {
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
            }
        }
    }

    private List<Station> toStations(List<Section> sections) {
        List<Station> upStations = sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
        List<Station> downStations = sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
        return Stream.concat(upStations.stream(), downStations.stream())
                .distinct()
                .collect(Collectors.toList());
    }

    // 다익스트라 알고리즘을 활용하여 최단 경로 계산
    public List<Station> getStations(Station start, Station end) {
        validateIsConnected(start, end);
        validateIsDifferentStation(start, end);
        return new DijkstraShortestPath<>(graph).getPath(start, end).getVertexList();
    }

    // 다익스트라 알고리즘을 활용하여 최단 거리 계산
    public long getDistance(Station start, Station end) {
        validateIsConnected(start, end);
        validateIsDifferentStation(start, end);
        return (long) new DijkstraShortestPath<>(graph).getPath(start, end).getWeight();
    }

    private void validateIsConnected(Station start, Station end) {
        if (!graph.containsVertex(start) || !graph.containsVertex(end)) {
            throw new IllegalArgumentException("출발역 또는 도착역이 연결되어 있지 않습니다");
        }
    }

    private void validateIsDifferentStation(Station start, Station end) {
        if (start.equals(end)) {
            throw new IllegalArgumentException("출발역과 도착역이 같습니다.");
        }
    }
}
