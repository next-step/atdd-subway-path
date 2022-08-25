package nextstep.subway.domain;

import nextstep.subway.error.exception.BusinessException;
import nextstep.subway.error.exception.ErrorCode;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collection;
import java.util.List;

public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath dijkstraShortestPath;
    private final List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        this.lines = lines;
        this.dijkstraShortestPath = new DijkstraShortestPath(this.graph);
        init(lines);
    }

    public Path findPath(Station source, Station target) {
        validateSourceAndTarget(source, target);
        final GraphPath shortestPath = findShortestPath(source, target);
        validateIsDisconnect(shortestPath);
        return new Path(shortestPath);
    }

    private void init(List<Line> lines) {
        initVertex(lines);
        initEdgeWeight(lines);
    }

    private void validateSourceAndTarget(Station source, Station target) {
        validateIsExistsStation(source, target);
        validateIsSourceAndTargetSame(source, target);
    }

    private void validateIsSourceAndTargetSame(Station source, Station target) {
        if (source.equals(target)) {
            throw new BusinessException(ErrorCode.SOURCE_AND_TARGET_IS_SAME);
        }
    }

    private void validateIsDisconnect(GraphPath graphPath) {
        if (graphPath == null) {
            throw new BusinessException(ErrorCode.SOURCE_AND_TARGET_IS_DISCONNECTED);
        }
    }

    private void validateIsExistsStation(Station source, Station target) {
        if (!isExistsStation(source) || !isExistsStation(target)) {
            throw new BusinessException(ErrorCode.STATION_NOT_FOUND_IN_SECTION);
        }
    }

    private Boolean isExistsStation(Station station) {
        return this.lines.stream()
                .map(it -> it.getStations().getList())
                .flatMap(Collection::stream)
                .anyMatch(it -> it.equals(station));
    }

    private void initVertex(List<Line> lines) {
        lines.stream()
                .map(it -> it.getStations().getList())
                .flatMap(Collection::stream)
                .forEach(this.graph::addVertex);
    }

    private void initEdgeWeight(List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .flatMap(Collection::stream)
                .forEach(section -> graph.setEdgeWeight(
                        graph.addEdge(section.getUpStation(), section.getDownStation()),
                        section.getDistance()));
    }

    private GraphPath findShortestPath(Station source, Station target) {
        return dijkstraShortestPath.getPath(source, target);
    }
}
