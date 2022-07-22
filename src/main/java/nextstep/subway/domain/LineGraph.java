package nextstep.subway.domain;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class LineGraph {

    private final Line line;
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public LineGraph(final Line line) {
        this.line = line;
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        init();
    }

    private void init() {
        addStationToVertex();
        addSectionToEdge();
    }

    private void addStationToVertex() {
        line.getStations().forEach(graph::addVertex);
    }

    private void addSectionToEdge() {
        line.getSections().forEach(v -> graph.setEdgeWeight(graph.addEdge(v.getUpStation(), v.getDownStation()), v.getDistance()));
    }

    public List<Station> findShortestPath(final Station from, final Station to) {
        final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(from, to).getVertexList();
    }
}
