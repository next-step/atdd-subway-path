package nextstep.subway.domain;

import nextstep.subway.exception.PathIllegalStationException;
import nextstep.subway.exception.PathNotSameStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

public class PathFinder {
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathFinder(List<Line> lines) {
        dijkstraShortestPath = makeDijkstraShortestPath(lines);
    }

    private DijkstraShortestPath<Station, DefaultWeightedEdge> makeDijkstraShortestPath(List<Line> lines) {
        WeightedMultigraph graph = drawGraph(lines);
        return new DijkstraShortestPath<>(graph);
    }

    private WeightedMultigraph drawGraph(List<Line> lines) {
        WeightedMultigraph graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        addVertex(lines, graph);
        addEdge(lines, graph);
        return graph;
    }

    private void addVertex(List<Line> lines, WeightedMultigraph graph) {
        lines.stream()
                .map(line -> line.getStations())
                .forEach(stations -> stations.stream()
                        .forEach(station -> graph.addVertex(station)));
    }

    private void addEdge(List<Line> lines, WeightedMultigraph graph) {
        lines.stream()
                .map(line -> line.getSections())
                .forEach(sections -> sections.stream()
                        .forEach(section -> graph.setEdgeWeight(
                                graph.addEdge(section.getUpStation(), section.getDownStation()),
                                section.getDistance())
                        )
                );
    }

    public Path findPath(Station source, Station target) {
        validateNotSameStation(source, target);
        try {
            GraphPath<Station, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);
            return new Path(
                    shortestPath.getVertexList(),
                    (int) shortestPath.getWeight()
            );
        } catch (IllegalArgumentException e) {
            throw new PathIllegalStationException();
        }
    }

    private void validateNotSameStation(Station source, Station target) {
        if (source.equals(target)) {
            throw new PathNotSameStationException();
        }
    }
}
