package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class PathFinder {

    private GraphPath graphPath;

    public static final PathFinder create(List<Section> sections, Station source, Station target) {
        return new PathFinder(getStations(sections), sections, source, target);
    }

    private PathFinder(List<Station> stations, List<Section> sections, Station source, Station target) {
        validateSourceTargetNotSame(source, target);
        generateGraphPath(stations, sections, source, target);
    }

    private void generateGraphPath(List<Station> stations, List<Section> sections, Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

        stations.forEach(it -> addVertex(graph, it));
        sections.forEach(it -> setEdgeWeight(graph, it));

        try {
            DijkstraShortestPath path = new DijkstraShortestPath(graph);
            graphPath = path.getPath(source, target);

            if (graphPath == null) {
                throw new RuntimeException("출발역과 도착역이 연결이 되어 있지 않은 경우 실패");
            }
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("존재하지 않은 출발역이나 도착역을 조회 할 경우 실패");
        }
    }

    private static List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .map(it -> it.getStations())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private void validateSourceTargetNotSame(Station source, Station target) {
        if (source == target) {
            throw new RuntimeException("시작역, 끝역 동일하면 실패");
        }
    }

    private void addVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Station station) {
        graph.addVertex(station);
    }

    private void setEdgeWeight(WeightedMultigraph<Station, DefaultWeightedEdge> graph, Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public List<Station> getShortestPath() {
        return graphPath.getVertexList();
    }

    public int getShortestPathDistance() {
        return (int) graphPath.getWeight();
    }
}
