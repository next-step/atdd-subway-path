package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        Lines lines = new Lines(lineRepository.findAll());
        Station sourceStation = stationService.findById(sourceStationId);
        Station targetStation = stationService.findById(targetStationId);

        PathFinder pathFinder = new PathFinder(lines);
        GraphPath<Station, DefaultWeightedEdge> shortestPath = pathFinder.getShortestPath(sourceStation, targetStation);
        return new PathResponse(createStationResponses(shortestPath.getVertexList()), shortestPath.getWeight());
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());
    }
}
