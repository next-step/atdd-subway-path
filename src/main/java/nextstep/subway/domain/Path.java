package nextstep.subway.domain;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Comparator;
import java.util.List;

public class Path {

    private final List<Line> lines;
    private final Station upStation;
    private final Station downStation;

    private final GraphPath<Station, DefaultWeightedEdge> graphPath;

    private Path(List<Line> lines, Station upStation, Station downStation) {
        this.lines = lines;
        this.upStation = upStation;
        this.downStation = downStation;
        var graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        addStation(graph);
        addSection(graph);
        this.graphPath = new KShortestPaths<>(graph, 100).getPaths(upStation, downStation).stream()
                .min(Comparator.comparing(GraphPath::getWeight))
                .orElseThrow(IllegalArgumentException::new);

    }

    public static Path of(List<Line> lines, Station upStation, Station downStation) {
        return new Path(lines, upStation, downStation);
    }

    public List<Station> getShortestPath() {
        return graphPath.getVertexList();
    }

    public int getWeight() {
        return (int) graphPath.getWeight();
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
