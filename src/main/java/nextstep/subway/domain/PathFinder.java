package nextstep.subway.domain;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class PathFinder {
    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
    }

    public List<Station> shortsPathStations(Station source, Station target) {
        return createDijkstraShortestPath().getPath(source, target).getVertexList();
    }

    public int shortsPathDistance(Station source, Station target) {
        return (int)createDijkstraShortestPath().getPathWeight(source, target);
    }

    private DijkstraShortestPath createDijkstraShortestPath() {
        Set<Station> stations = new HashSet<>();
        List<Section> sections = new ArrayList<>();
        for (Line line : lines) {
            stations.addAll(line.getStations());
            sections.addAll(line.getSections());
        }

        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        for (Station station : stations) {
            graph.addVertex(station);
        }
        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
        return new DijkstraShortestPath(graph);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathFinder that = (PathFinder) o;
        return Objects.equals(lines, that.lines);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lines);
    }
}
