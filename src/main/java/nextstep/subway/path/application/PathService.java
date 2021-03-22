package nextstep.subway.path.application;

import nextstep.subway.path.domain.Path;
import nextstep.subway.path.domain.SubwayGraph;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.application.StationService;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {

    private StationService stationService;
    private GraphService graphService;

    public PathService(StationService stationService, GraphService graphService) {
        this.stationService = stationService;
        this.graphService = graphService;
    }

    public PathResponse findShortestPath(Long sourceId, Long targetId) {
        SubwayGraph subwayGraph = graphService.findGraph();

        Station source = stationService.findById(sourceId);
        Station target = stationService.findById(targetId);

        return createPathResponse(subwayGraph, source, target);
    }

    private PathResponse createPathResponse(SubwayGraph subwayGraph, Station source, Station target) {
        Path path = subwayGraph.getShortestPath(source, target);

        List<StationResponse> stationResponses = path.getStations().stream()
                .map(it -> StationResponse.of(it))
                .collect(Collectors.toList());

        return new PathResponse(stationResponses, path.getDistance());
    }
}
