package nextstep.subway.domain;

import lombok.RequiredArgsConstructor;
import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
    private final DijkstraShortestPath dijkstraShortestPath;

    private PathFinder() {
        dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public PathFinder(final List<Line> lines) {
        init(lines);
        this.dijkstraShortestPath = new DijkstraShortestPath(graph);
    }

    public List<Station> findShortestPath(final Station sourceStation, final Station targetStation) {
        validateBeforeFindPath(sourceStation, targetStation);
        return dijkstraShortestPath.getPath(sourceStation, targetStation).getVertexList();
    }

    public int findShortestPathDistance(final Station sourceStation, final Station targetStation) {
        validateBeforeFindPath(sourceStation, targetStation);
        return (int) dijkstraShortestPath.getPath(sourceStation, targetStation).getWeight();
    }

    private void init(final List<Line> lines) {
        for (final Line line : lines) {
            final List<Station> stations = line.getStations();
            stations.forEach(graph::addVertex);
            final List<Section> sections = line.getSections();
            sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        }
    }

    private void validateBeforeFindPath(final Station source, final Station target) {
        if (source.equals(target)) {
            throw new BusinessException(ErrorCode.SOURCE_AND_TARGET_STATION_IS_SAME);
        }
        if (!graph.vertexSet().contains(source) | !graph.vertexSet().contains(target)) {
            throw new BusinessException(ErrorCode.STATION_NOT_FOUND);
        }
        if (Objects.isNull(dijkstraShortestPath.getPath(source, target))) {
            throw new BusinessException(ErrorCode.SOURCE_AND_TARGET_STATION_IS_NOT_CONNECTED);
        }
    }
}
