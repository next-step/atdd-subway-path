package nextstep.subway.domain.entity;

import java.util.List;
import lombok.Getter;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

@Getter
public class PathFinder {

    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }


    public Path findShortestPath(Station source, Station target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addStationsAsVerticesToGraph(graph);
        addEdgeWeightsToGraph(graph);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> result = dijkstraShortestPath.getPath(source, target);
        return new Path(result.getVertexList(), result.getWeight());

    }

    private void addStationsAsVerticesToGraph(Graph<Station, DefaultWeightedEdge> graph) {
        this.lines.stream()
            .flatMap(line -> line.getAllStationsByDistinct().stream())
            .forEach(graph::addVertex);
    }

    private void addEdgeWeightsToGraph(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        this.lines.stream()
            .flatMap(line -> line.getSections().getAllSections().stream())
            .forEach(section -> graph.setEdgeWeight(
                graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }
}
