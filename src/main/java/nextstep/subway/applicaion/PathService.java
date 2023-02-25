package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long source, Long target) {
        final List<Line> lines = lineService.findAllLine();

        WeightedMultigraph<Long, DefaultWeightedEdge> graph = new WeightedMultigraph(DefaultWeightedEdge.class);
        lines.forEach(
            line -> line.getSections().getElements()
                .forEach(
                    section -> {
                        final Long upStationId = section.getUpStationId();
                        final Long downStationId = section.getDownStationId();

                        graph.addVertex(upStationId);
                        graph.addVertex(downStationId);
                        graph.setEdgeWeight(graph.addEdge(upStationId, downStationId), section.getDistance());
                    }
                )
        );

        DijkstraShortestPath<Long, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        final GraphPath<Long, DefaultWeightedEdge> shortestPath = dijkstraShortestPath.getPath(source, target);
        final List<Long> stationIds = shortestPath.getVertexList();
        final double distance = shortestPath.getWeight();

        final List<StationResponse> stationResponses = stationIds.stream()
            .map(it -> stationService.findStationById(it))
            .collect(Collectors.toList());

        return new PathResponse(stationResponses, distance);
    }
}
