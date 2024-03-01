package nextstep.subway.domain.entity;

import java.util.List;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

@Slf4j
@Getter
public class PathFinder {

    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }


    public Path findShortestPath(Station source, Station target) {
        if (source == target) {
            throw new IllegalArgumentException("Source and target stations are the same");
        }
        WeightedGraph<Station, DefaultWeightedEdge> graph = createWeightedGraph();
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        try {
            GraphPath<Station, DefaultWeightedEdge> result = dijkstraShortestPath.getPath(source, target);
            return new Path(result.getVertexList(), result.getWeight());
        } catch (Exception e) {
            log.warn("Failed to find the shortest path. Source station: {}, Target station: {}", source.getId(),
                target.getId(), e);
            throw new IllegalStateException("Unable to find the shortest path.");
        }


    }

    private SimpleWeightedGraph<Station, DefaultWeightedEdge> createWeightedGraph() {
        SimpleWeightedGraph<Station, DefaultWeightedEdge> graph = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
        addStationsAsVerticesToGraph(graph);
        addEdgeWeightsToGraph(graph);
        return graph;
    }

    private void addStationsAsVerticesToGraph(Graph<Station, DefaultWeightedEdge> graph) {
        this.lines.stream()
            .flatMap(line -> line.getAllStationsByDistinct().stream())
            .forEach(graph::addVertex);
    }

    private void addEdgeWeightsToGraph(WeightedGraph<Station, DefaultWeightedEdge> graph) {
        this.lines.stream()
            .flatMap(line -> line.getSections().getAllSections().stream())
            .forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
