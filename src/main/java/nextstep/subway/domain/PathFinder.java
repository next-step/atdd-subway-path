package nextstep.subway.domain;

import nextstep.subway.domain.vo.Path;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.*;

public class PathFinder {

    private final List<Section> sections;

    public PathFinder(List<Section> sections) {
        this.sections = sections;
    }

    public Path getShortestPath(Station sourceStation, Station targetStation) {
        return initializePath(sourceStation, targetStation)
                .map(path -> {
                    List<Station> stations = path.getVertexList();
                    long distance = (long) path.getWeight();
                    return new Path(stations, distance);
                })
                .orElseThrow(IllegalArgumentException::new);
    }

    private Optional<GraphPath<Station, DefaultWeightedEdge>> initializePath(Station sourceStation, Station targetStation) {
        if (Objects.equals(sourceStation, targetStation)) {
            return Optional.empty();
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        addStationVertexes(graph);
        addSectionEdges(graph);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return Optional.ofNullable(dijkstraShortestPath.getPath(sourceStation, targetStation));
    }

    private void addStationVertexes(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .map(section -> Set.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .forEach(graph::addVertex);
    }

    private void addSectionEdges(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
