package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.exception.NotConnectedException;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;

public class PathFinder {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    private List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        initialize();
    }

    public GraphPath getShortestPath(Station source, Station target) {
        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);
        return Optional
                .ofNullable(dijkstraShortestPath.getPath(source, target))
                .orElseThrow(NotConnectedException::new);
    }

    private void initialize() {
        lines.stream().map(Line::getAllStations).forEach(this::addVertex);
        lines.stream().map(line -> line.getSections().getSections()).forEach(this::setEdgeWeight);
    }

    private void setEdgeWeight(List<Section> sections) {
        sections.forEach(section ->
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private void addVertex(List<Station> stations) {
        stations.forEach(graph::addVertex);
    }
}

