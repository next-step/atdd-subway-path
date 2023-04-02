package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.stream.Collectors;

public class SubwayMap {

    private DijkstraShortestPath<Long, DefaultWeightedEdge> subwayMap;

    public SubwayMap(List<Line> lines) {
        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        addStationToMap(graph, lines);
        addSectionToMap(graph, lines);

        this.subwayMap = new DijkstraShortestPath<>(graph);
    }

    private void addStationToMap(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Line> lines) {
        List<Station> stations = lines.stream()
            .flatMap(it -> it.getSortedStations().stream())
            .collect(Collectors.toList());

        for (Station station : stations) {
            graph.addVertex(station.getId());
        }
    }

    private void addSectionToMap(WeightedMultigraph<Long, DefaultWeightedEdge> graph, List<Line> lines) {
        List<Section> sections = lines.stream()
            .flatMap(it -> it.getSections().stream())
            .collect(Collectors.toList());

        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation().getId(), section.getDownStation().getId()),
                section.getDistance());
        }
    }

    public List<Long> findPath(Station source, Station target) {
        return subwayMap.getPath(source.getId(), target.getId()).getVertexList();
    }

    public int calculateDistance(Station source, Station target) {
        try {
            return (int)subwayMap.getPathWeight(source.getId(), target.getId());
        } catch (Exception e) {
            throw new IllegalArgumentException("연결되지 않은 역입니다.");
        }
    }
}
