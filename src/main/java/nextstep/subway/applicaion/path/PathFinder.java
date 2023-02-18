package nextstep.subway.applicaion.path;

import nextstep.subway.applicaion.dto.path.PathResponse;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.section.Section;
import nextstep.subway.domain.station.Station;
import nextstep.subway.exception.SubwayRestApiException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.List;

import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_NO_CONNECTION_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_NO_EXIST_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_SAME_SOURCE_TARGET;

public class PathFinder {
    private static WeightedMultigraph<Station, DefaultWeightedEdge> graph;
    private static ShortestPathAlgorithm shortestPath;

    private PathFinder() {
    }

    public static PathResponse findPath(List<Line> lines, Station source, Station target) {
        initGraph(lines);

        shortestPath = new DijkstraShortestPath(graph);
        validation(source, target);

        GraphPath<Station, Double> graphPath = shortestPath.getPath(source, target);

        return PathResponse.of(new Path(graphPath.getVertexList(), graphPath.getWeight()));
    }

    private static void initGraph(List<Line> lines) {
        graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);

        lines.stream()
                .map(Line::getSections)
                .forEach(a -> addGraphValue(a.getSections()));
    }

    private static void addGraphValue(List<Section> sections) {
        for (Section section : sections) {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());

            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()), section.getDistance());
        }
    }

    private static void validation(Station source, Station target) {
        if (source.equals(target)) {
            throw new SubwayRestApiException(ERROR_FOUND_PATH_SAME_SOURCE_TARGET);
        }

        if (!graph.containsVertex(source) || !graph.containsVertex(target)) {
            throw new SubwayRestApiException(ERROR_FOUND_PATH_NO_EXIST_STATION);
        }

        if (null == shortestPath.getPath(source, target)) {
            throw new SubwayRestApiException(ERROR_FOUND_PATH_NO_CONNECTION_STATION);
        }
    }
}
