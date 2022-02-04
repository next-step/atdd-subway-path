package nextstep.subway.domain;

import nextstep.subway.exception.path.NotFoundPathException;
import nextstep.subway.exception.path.SameStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Objects;

public class ShortestPathChecker {

    private WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    private ShortestPathChecker() {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public static ShortestPathChecker of(List<Line> lines) {
        ShortestPathChecker pathChecker = new ShortestPathChecker();
        pathChecker.initGraph(lines);

        return pathChecker;
    }

    public List<Station> findShortestPath(Station source, Station target) {
        validatePath(source, target);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);

        if (Objects.isNull(path)) {
            throw new NotFoundPathException(source.getName(), target.getName());
        }

        return path.getVertexList();
    }

    private void initGraph(List<Line> lines) {
        lines.forEach(this::initVertex);
        lines.forEach(this::initEdgeWeight);
    }

    private void initVertex(Line line) {
        line.getStations()
                .forEach(graph::addVertex);
    }

    private void initEdgeWeight(Line line) {
        List<Section> sections = line.getAllSection();
        sections.stream()
                .forEach(it -> graph.setEdgeWeight(
                        graph.addEdge(it.getUpStation(), it.getDownStation()), it.getDistance()));
    }

    private void validatePath(Station source, Station target) {
        if (Objects.equals(source, target)) {
            throw new SameStationException();
        }
    }

}
