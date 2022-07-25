package nextstep.subway.domain;

import nextstep.subway.exception.paths.CannotFindPathException;
import nextstep.subway.exception.paths.EmptyLineException;
import nextstep.subway.exception.paths.NotConnectedPathException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

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
        if (startStation == null || arrivalStation == null) {
            throw new CannotFindPathException();
        }

        GraphPath path = dijkstraShortestPath.getPath(startStation, arrivalStation);
        if (path == null) {
            throw new NotConnectedPathException();
        }

        return path.getVertexList();
    }

    public int getShortestDistance(Station startStation, Station arrivalStation) {
        if (startStation == null || arrivalStation == null) {
            throw new CannotFindPathException();
        }
        return (int) dijkstraShortestPath.getPath(startStation, arrivalStation).getWeight();
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
}
