package nextstep.subway.domain;

import nextstep.subway.error.ErrorCode;
import nextstep.subway.error.exception.BusinessException;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private final DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath;

    public PathFinder(final List<Line> lines) {
        init(lines);
        graph = new WeightedMultigraph<Station, DefaultWeightedEdge>(DefaultWeightedEdge.class);
        dijkstraShortestPath = new DijkstraShortestPath<Station, DefaultWeightedEdge>(graph);
    }

    public List<Station> findShortestPath(final Station sourceStation, final Station targetStation) {
        final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        validateBeforeFindPath(sourceStation, targetStation, path);
        return path.getVertexList();
    }

    public int findShortestPathDistance(final Station sourceStation, final Station targetStation) {
        final GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(sourceStation, targetStation);
        validateBeforeFindPath(sourceStation, targetStation, path);
        return (int) path.getWeight();
    }

    private void init(final List<Line> lines) {
        for (final Line line : lines) {
            final List<Station> stations = line.getStations();
            stations.forEach(graph::addVertex);
            final List<Section> sections = line.getSections();
            sections.forEach(section -> graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance()));
        }
    }

    private void validateBeforeFindPath(final Station source, final Station target, final GraphPath<Station, DefaultWeightedEdge> path) {
        if (source.equals(target)) {
            throw new BusinessException(ErrorCode.SOURCE_AND_TARGET_STATION_IS_SAME);
        }
        if (!graph.vertexSet().contains(source) | !graph.vertexSet().contains(target)) {
            throw new BusinessException(ErrorCode.STATION_NOT_FOUND);
        }
        if (Objects.isNull(path)) {
            throw new BusinessException(ErrorCode.SOURCE_AND_TARGET_STATION_IS_NOT_CONNECTED);
        }
    }
}
