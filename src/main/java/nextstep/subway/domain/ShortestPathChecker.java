package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class ShortestPathChecker {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    private ShortestPathChecker() {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public static ShortestPathChecker of(List<Line> lines) {
        ShortestPathChecker pathChecker = new ShortestPathChecker();
        pathChecker.initGraph(lines);

        return pathChecker;
    }

    public List<Station> findShortestPath(Station source, Station target) {
        return dijkstraShortestPath.getPath(source, target)
                .getVertexList();
    }

    private void initGraph(List<Line> lines) {
        lines.forEach(this::initVertex);
        lines.forEach(this::initEdgeWeight);
    }

    private void initVertex(Line line) {
        line.getStations()
                .forEach(graph::addVertex);
    }

    private void initEdgeWeight(Line line) {
        List<Section> sections = line.getAllSection();
        sections.stream()
                .forEach(it -> graph.setEdgeWeight(
                        graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance()));
    }

}
