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
        createVertex();
        createEdge();
    }

    private void createVertex() {
        List<Station> stations = lines.stream()
                .flatMap(line -> line.getStations().stream())
                .distinct()
                .collect(Collectors.toList());

        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    private void createEdge() {
        List<Section> sections = lines.stream()
                .flatMap(line -> line.getSections().getSections().stream())
                .collect(Collectors.toList());

        for (Section section : sections) {
            graph.setEdgeWeight(graph.addEdge(section.getUpStation(), section.getDownStation()),
                    section.getDistance());
        }
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
