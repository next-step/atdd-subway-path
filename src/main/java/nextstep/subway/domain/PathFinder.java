package nextstep.subway.domain;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.applicaion.constants.ErrorMessage;
import nextstep.subway.applicaion.exception.PathFinderException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class PathFinder {

    private List<Section> sections;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Section> sections) {
        this.sections = sections;
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        setVertex(graph);
        setEdge(graph);
    }

    private void setVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .map(section -> Set.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .forEach(graph::addVertex);
    }

    private void setEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Section section : sections) {
            DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance());
        }
    }

    public GraphPath findPath(Station sourceStation, Station targetStation) {
        if(sourceStation.isEqual(targetStation)) {
            throw new PathFinderException(ErrorMessage.SAME_BETWEEN_STATIONS);
        }

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        validatePath(sourceStation, targetStation);

        GraphPath shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (Objects.isNull(shortestPath)) {
            throw new PathFinderException(ErrorMessage.NOT_FOUND_PATH);
        }
        return shortestPath;
    }

    private void validatePath(Station sourceStation, Station targetStation) {

        if (!graph.containsVertex(sourceStation)) {
            throw new PathFinderException(ErrorMessage.NO_LINK_START_STATION);
        }

        if (!graph.containsVertex(targetStation)) {
            throw new PathFinderException(ErrorMessage.NO_LINK_TARGET_STATION);
        }
    }
}