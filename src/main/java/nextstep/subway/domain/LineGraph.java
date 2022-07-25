package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class LineGraph {

    private final List<Line> lines;
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public LineGraph(final List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        init();
    }

    private void init() {
        addStationToVertex();
        addSectionToEdge();
    }

    private void addStationToVertex() {
        for (final Line line : lines) {
            line.getStations().forEach(this::addVertex);
        }
    }

    private void addVertex(final Station station) {
        graph.addVertex(station);
    }

    private void addSectionToEdge() {
        for (final Line line : lines) {
            line.getSections().forEach(this::addEdge);
        }
    }

    private void addEdge(final Section section) {
        graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
    }

    public StationPath findShortestPath(final Station from, final Station to) {
        if (from.equals(to)) {
            throw new IllegalArgumentException("From station and to station is same");
        }

        final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(from, to);
        return new StationPath(path.getVertexList(), (long) path.getWeight());
    }
}
