package nextstep.subway.domain.path;

import lombok.extern.slf4j.Slf4j;
import nextstep.subway.domain.line.Section;
import nextstep.subway.domain.station.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Slf4j
public class PathFinder {

    private List<Section> sections;
    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Section> sections) {
        this.sections = sections;
        initPath();
    }

    private void initPath() {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        initVertex(graph);
        initEdge(graph);
    }

    private void initVertex(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        sections.stream()
                .map(section -> Set.of(section.getUpStation(), section.getDownStation()))
                .flatMap(Collection::stream)
                .forEach(graph::addVertex);
    }

    private void initEdge(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        for (Section section : sections) {
            DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
            graph.setEdgeWeight(edge, section.getDistance());
        }
    }

    public GraphPath findPath(Station sourceStation, Station targetStation) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        GraphPath shortestPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        return shortestPath;
    }
}