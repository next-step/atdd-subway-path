package nextstep.subway.path.application;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.common.exception.SubwayException;
import nextstep.subway.common.exception.SubwayExceptionType;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.application.dto.StationResponse;
import nextstep.subway.station.domain.Station;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class ShortestPathFinder implements PathFinder {

    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(
        DefaultWeightedEdge.class);

    @Override
    public PathResponse find(List<Line> lines, Station source, Station target) {

        validateSourceAndTargetStations(source, target);

        setUpGraph(lines, graph);
        GraphPath<Station, DefaultWeightedEdge> path = findShortestPath(
            source, target, graph);

        return getPathResponse(path);
    }

    private static void validateSourceAndTargetStations(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayException(SubwayExceptionType.SOURCE_AND_TARGET_SAME);
        }
    }

    private void setUpGraph(List<Line> lines,
        WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        addVertex(lines, graph);
        addEdge(lines, graph);
    }

    private void addVertex(List<Line> lines,
        WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
            .flatMap(line -> line.getLineSections().getStations().stream())
            .forEach(graph::addVertex);
    }

    private void addEdge(List<Line> lines,
        WeightedMultigraph<Station, DefaultWeightedEdge> graph) {
        lines.stream()
            .flatMap(line -> line.getLineSections().stream())
            .forEach(section -> {
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(),
                    section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            });
    }

    private GraphPath<Station, DefaultWeightedEdge> findShortestPath(
        Station source, Station target, WeightedMultigraph<Station, DefaultWeightedEdge> graph) {

        validateVerticesExist(graph, source, target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(
            graph);
        GraphPath<Station, DefaultWeightedEdge> path = dijkstraShortestPath.getPath(source, target);

        if (path == null) {
            throw new SubwayException(SubwayExceptionType.PATH_NOT_FOUND);
        }
        return path;
    }

    private void validateVerticesExist(WeightedMultigraph<Station, DefaultWeightedEdge> graph,
        Station source, Station target) {
        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new SubwayException(SubwayExceptionType.PATH_NOT_FOUND);
        }
    }

    private PathResponse getPathResponse(GraphPath<Station, DefaultWeightedEdge> path) {
        List<Station> stations = path.getVertexList();
        double distance = path.getWeight();

        return new PathResponse(
            stations.stream().map(StationResponse::from).collect(
                Collectors.toList())
            , (long) distance);
    }
}
