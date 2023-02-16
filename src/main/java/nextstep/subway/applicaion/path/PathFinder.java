package nextstep.subway.applicaion.path;

import nextstep.subway.applicaion.dto.path.PathResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.SubwayRestApiException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_NO_CONNECTION_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_NO_EXIST_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_SAME_SOURCE_TARGET;

public class PathFinder {
    private static WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    private PathFinder() {
    }

    public static PathResponse findPath(List<Line> lines, Long source, Long target) {
        Path path = searchShortPath(lines, source, target);

        return PathResponse.of(path);
    }

    private static void initGraph(List<Line> lines) {
        lines.stream()
                .map(Line::getSections)
                .forEach(a -> addGraphValue(a));
    }

    private static void addGraphValue(List<Section> sections) {
        for (Section section : sections) {
            String vertex1 = String.valueOf(section.getUpStation().getId());
            String vertex2 = String.valueOf(section.getDownStation().getId());
            graph.addVertex(vertex1);
            graph.addVertex(vertex2);

            graph.setEdgeWeight(graph.addEdge(vertex1, vertex2), section.getDistance());
        }
    }

    private static Path searchShortPath(List<Line> lines, Long sourceId, Long targetId) {
        initGraph(lines);

        DijkstraShortestPath dijkstraShortestPath = new DijkstraShortestPath(graph);

        validation(graph, dijkstraShortestPath, sourceId, targetId);

        GraphPath<Object, Object> graphPath = dijkstraShortestPath.getPath(String.valueOf(sourceId), String.valueOf(targetId));

        List<String> stationIds = graphPath.getVertexList().stream()
                .map(a -> String.valueOf(a))
                .collect(Collectors.toList());

        int weight = (int) graphPath.getWeight();

        Path path = new Path(stationIds.stream()
                .map(a -> findStation(lines, Long.valueOf(a)))
                .filter(a -> null != a)
                .collect(Collectors.toList()), weight);

        return path;
    }

    private static Station findStation(List<Line> lines, Long stationId) {
        for (Line line : lines) {
            Optional<Station> opStation = line.getStations().stream()
                    .filter(a -> stationId.equals(a.getId()))
                    .findFirst();

            if (opStation.isPresent()) {
                return opStation.get();
            }
        }

        return null;
    }

    private static void validation(WeightedMultigraph<String, DefaultWeightedEdge> graph, DijkstraShortestPath path, Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            throw new SubwayRestApiException(ERROR_FOUND_PATH_SAME_SOURCE_TARGET);
        }

        if (!graph.containsVertex(String.valueOf(sourceId)) || !graph.containsVertex(String.valueOf(targetId))) {
            throw new SubwayRestApiException(ERROR_FOUND_PATH_NO_EXIST_STATION);
        }

        if (null == path.getPath(String.valueOf(sourceId), String.valueOf(targetId))) {
            throw new SubwayRestApiException(ERROR_FOUND_PATH_NO_CONNECTION_STATION);
        }
    }
}
