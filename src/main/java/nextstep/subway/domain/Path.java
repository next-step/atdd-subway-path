package nextstep.subway.domain;

import java.util.Comparator;
import java.util.List;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.KShortestPaths;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class Path {

    private final List<Line> lines;
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final Station startStation;
    private final Station endStation;

    public Path(final List<Line> lines, final Station startStation, final Station endStation) {
        this.lines = lines;
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        this.startStation = startStation;
        this.endStation = endStation;
        addStations();
        addSections();
    }

    private void addStations() {
        lines.stream()
            .flatMap(line -> line.getStations().stream())
            .distinct()
            .forEach(graph::addVertex);
    }

    private void addSections() {
        lines.stream()
            .flatMap(line -> line.getSections().sections().stream())
            .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(),
                section.getDownStation()), section.getDistance()));
    }

    public List<Station> getShortestPath() {
        return getStationsShortestPath().getVertexList();
    }

    public int getTotalDistance() {
        return (int) getStationsShortestPath().getWeight();
    }

    private GraphPath<Station, DefaultWeightedEdge> getStationsShortestPath() {
        final List<GraphPath<Station, DefaultWeightedEdge>> paths = new KShortestPaths<>(graph, 100)
            .getPaths(startStation, endStation);

        return paths.stream()
            .min(Comparator.comparing(GraphPath::getWeight))
            .orElseThrow(IllegalArgumentException::new);
    }

}
