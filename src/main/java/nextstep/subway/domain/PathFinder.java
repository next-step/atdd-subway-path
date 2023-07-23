package nextstep.subway.domain;

import nextstep.subway.exception.InvalidStationException;
import nextstep.subway.exception.NotFoundPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.*;

public class PathFinder {
    private DijkstraShortestPath dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = getGraph(lines);
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public static PathFinder of(List<Line> lines) {
        return new PathFinder(lines);
    }

    public Path getShortestPath(Station sourceStation, Station targetStation) {
        validate(sourceStation, targetStation);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(sourceStation, targetStation);
        if (Objects.isNull(graphPath)) {
            throw new NotFoundPathException();
        }
        return Path.of(graphPath.getVertexList(), (int) graphPath.getWeight());
    }

    private WeightedMultigraph<Station, DefaultWeightedEdge> getGraph(List<Line> lines) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        addVertexAll(graph, lines);
        setEdgeWeightAll(graph, lines);
        return graph;
    }

    private void addVertexAll(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        getStations(lines).forEach(graph::addVertex);
    }

    private void setEdgeWeightAll(WeightedMultigraph<Station, DefaultWeightedEdge> graph, List<Line> lines) {
        lines.forEach(line -> line.getSections()
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation()),
                        section.getDistance())));
    }

    private List<Station> getStations(List<Line> lines) {
        Set<Station> stationSet = new HashSet<>();
        lines.forEach(line -> stationSet.addAll(line.getStations()));
        return new ArrayList<>(stationSet);
    }

    private void validate(Station sourceStation, Station targetStation) {
        if (isInvalidStationId(sourceStation, targetStation)) {
            throw new InvalidStationException();
        }
    }

    private boolean isInvalidStationId(Station sourceStation, Station targetStation) {
        return Objects.isNull(sourceStation) || Objects.isNull(targetStation) || sourceStation.equals(targetStation);
    }
}
