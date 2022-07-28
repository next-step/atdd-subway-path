package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Comparator;
import java.util.List;

public class Path {

    private final List<Line> lines;

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private Path(List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addStation(graph);
        addSection(graph);
    }

    public static Path of(List<Line> lines) {
        return new Path(lines);
    }

    public GraphPath<Station, DefaultWeightedEdge> getShortestPath(Station upStation, Station downStation) {
        return new KShortestPaths<>(graph, 100).getPaths(upStation, downStation).stream()
                .min(Comparator.comparing(GraphPath::getWeight))
                .orElseThrow(IllegalArgumentException::new);
    }

    private void addSection(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream().flatMap(line -> line.getSections().stream())
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void addStation(WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .forEach(graph::addVertex);
    }

}
