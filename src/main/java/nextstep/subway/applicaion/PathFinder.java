package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;
import nextstep.subway.exception.SubwayRestApiException;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_NO_CONNECTION_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_NO_EXIST_STATION;
import static nextstep.subway.exception.ErrorResponseEnum.ERROR_FOUND_PATH_SAME_SOURCE_TARGET;

@Component
public class PathFinder {
    private WeightedMultigraph<String, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);

    public PathFinder() {
    }

    public void initGraph(List<Line> lines) {
        lines.stream().map(Line::getSections).forEach(a -> addGraphValue(a));
    }

    private void addGraphValue(Sections sections) {
        for (Section section : sections.getSections()) {
            String vertex1 = String.valueOf(section.getUpStation().getId());
            String vertex2 = String.valueOf(section.getDownStation().getId());
            graph.addVertex(vertex1);
            graph.addVertex(vertex2);

            graph.setEdgeWeight(graph.addEdge(vertex1, vertex2), section.getDistance());
        }
    }

    public Path searchShortPath(Long sourceId, Long targetId) {
        DijkstraShortestPath path = new DijkstraShortestPath(graph);

        validation(path, sourceId, targetId);

        GraphPath<Object, Object> graphPath = path.getPath(String.valueOf(sourceId), String.valueOf(targetId));

        List<String> stationIds = graphPath.getVertexList().stream().map(a -> String.valueOf(a)).collect(Collectors.toList());
        int weight = (int) graphPath.getWeight();

        return new Path(stationIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList()), weight);
    }

    private void validation(DijkstraShortestPath path, Long sourceId, Long targetId) {
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
