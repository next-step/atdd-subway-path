package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class LineGraph {

    private final Line line;
    private final List<Line> lines;
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public LineGraph(final List<Line> lines) {
        this.line = null;
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

    public List<Station> findShortestPath(final Station from, final Station to) {
        final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        return dijkstraShortestPath.getPath(from, to).getVertexList();
    }
}
