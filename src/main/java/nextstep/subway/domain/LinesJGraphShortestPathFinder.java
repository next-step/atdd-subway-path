package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.WeightedGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class LinesJGraphShortestPathFinder implements ShortestPathFinder<Station, Line, Integer> {

    private WeightedGraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

    private void initVertices(List<Line> vertices) {
        vertices.stream()
                .flatMap(line -> line.getStations().stream())
                .forEach(graph::addVertex);
    }

    private void initEdges(List<Section> edges) {
        edges.forEach(edge -> graph.setEdgeWeight(
                graph.addEdge(edge.getUpStation(), edge.getDownStation()),
                edge.getDistance()));
    }

    private void initGraph(List<Line> lines) {
        initVertices(lines);
        initEdges(lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList()));
    }

    @Override
    public ShortestPath<Station, Integer> findShortestPath(Station source, Station destination, List<Line> data) {
        initGraph(data);
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(source, destination);

        if (Objects.isNull(graphPath)) {
            return ShortestPath.NONE;
        }

        return ShortestPath.of(
                graphPath.getVertexList(),
                (int) graphPath.getWeight()
        );
    }
}
