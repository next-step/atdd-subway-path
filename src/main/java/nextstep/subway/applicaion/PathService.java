package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final StationService stationService;
    private final LineService lineService;

    public PathService(StationService stationService, LineService lineService) {
        this.stationService = stationService;
        this.lineService = lineService;
    }

    public PathResponse findPath(Long source, Long target) {
        WeightedMultigraph<Station, DefaultWeightedEdge> graph = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        List<Line> lines = lineService.findAll();

        for (Line line : lines) {
            line.getStations().forEach(graph::addVertex);
            line.getSections().forEach(section -> {
                DefaultWeightedEdge edge = graph.addEdge(section.getUpStation(), section.getDownStation());
                graph.setEdgeWeight(edge, section.getDistance());
            });
        }

        Station departure = stationService.findById(source);
        Station destination = stationService.findById(target);

        DijkstraShortestPath<Station, DefaultWeightedEdge> dijkstraShortestPath = new DijkstraShortestPath<>(graph);
        GraphPath<Station, DefaultWeightedEdge> result = dijkstraShortestPath.getPath(departure, destination);

        int distance = (int) dijkstraShortestPath.getPathWeight(departure, destination);
        List<StationResponse> stations = result.getVertexList()
            .stream()
            .map(StationResponse::new)
            .collect(Collectors.toList());

        return new PathResponse(stations, distance);
    }
}
