package nextstep.subway.domain;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

public class PathFinder {
    private final List<Line> lines;
    private final WeightedMultigraph<Station, DefaultWeightedEdge> graph;

    public PathFinder(List<Line> lines) {
        this.lines = lines;
        this.graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        init();
    }

    private void init() {
        lines.forEach(line -> {
            createVertex(line);
            createEdge(line);
        });
    }

    private void createVertex(Line line) {
        line.getSections().getSections().forEach(section -> {
            graph.addVertex(section.getUpStation());
            graph.addVertex(section.getDownStation());
        });
    }

    private void createEdge(Line line) {
        line.getSections().getSections().forEach(section ->
                graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                        section.getDistance())
        );
    }

    public PathResponse findPath(Station source, Station target) {
        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> graphPath = dijkstraShortestPath.getPath(source, target);

        if (graphPath == null) {
            throw new IllegalArgumentException("경로를 찾을 수 없습니다.");
        }

        List<StationResponse> stationResponses = graphPath.getVertexList().stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, (int) graphPath.getWeight());
    }
}
