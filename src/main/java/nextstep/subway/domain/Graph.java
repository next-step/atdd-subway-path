package nextstep.subway.domain;

import nextstep.subway.exception.paths.CannotFindPathException;
import nextstep.subway.exception.paths.EmptyLineException;
import nextstep.subway.exception.paths.NotConnectedPathException;
import nextstep.subway.exception.paths.SameStartArrivalStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class Graph {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath dijkstraShortestPath;

    public Graph(List<Line> lines) {
        if (lines.isEmpty()) {
            throw new EmptyLineException();
        }

        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
        initGraph(lines);
    }

    public List<Station> getShortestPath(Station startStation, Station arrivalStation) {
        GraphPath path = createGraphPath(startStation, arrivalStation);
        return path.getVertexList();
    }

    public int getShortestDistance(Station startStation, Station arrivalStation) {
        GraphPath path = createGraphPath(startStation, arrivalStation);
        return (int) path.getWeight();
    }

    private void initGraph(List<Line> lines) {
        initVertexes(lines);
        initEdges(lines);
    }

    private void initVertexes(List<Line> lines) {
        lines.stream()
                .map(Line::getStations)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(graph::addVertex);
    }

    private void initEdges(List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .distinct()
                .forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
    }

    private GraphPath createGraphPath(Station startStation, Station arrivalStation) {
        if (Objects.isNull(startStation) || Objects.isNull(arrivalStation)) {
            throw new CannotFindPathException();
        }

        if (startStation.equals(arrivalStation)) {
            throw new SameStartArrivalStationException();
        }

        GraphPath path = dijkstraShortestPath.getPath(startStation, arrivalStation);
        if (Objects.isNull(path)) {
            throw new NotConnectedPathException();
        }

        return path;
    }
}
