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

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
    private final DijkstraShortestPath path = new DijkstraShortestPath(graph);
    private GraphPath graphPath;

    public static final PathFinder create(List<Section> sections) {
        return new PathFinder(getStations(sections), sections);
    }

    private static List<Station> getStations(List<Section> sections) {
        return sections.stream()
                .map(it -> it.getStations())
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    private PathFinder(List<Station> stations, List<Section> sections) {
        stations.forEach(this::addVertex);
        sections.forEach(this::setEdgeWeight);
    }

    private void addVertex(Station station) {
        graph.addVertex(station);
    }

    private void setEdgeWeight(Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public void calculateShortestPath(Station source, Station target) {
        validatePath(source, target);
        graphPath = path.getPath(source, target);
    }

    private void validatePath(Station source, Station target) {
        validateSourceTargetNotSame(source, target);
    }

    private void validateSourceTargetNotSame(Station source, Station target) {
        if (source == target) {
            throw new RuntimeException();
        }
    }

    public List<Station> getShortestPath() {
        return graphPath.getVertexList();
    }

    public int getShortestPathDistance() {
        return (int) graphPath.getWeight();
    }
}
