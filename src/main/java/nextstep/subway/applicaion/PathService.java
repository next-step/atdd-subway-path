package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Graph;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse findShortestPath(Long source, Long target) {
        Station startStation = stationService.findById(source);
        Station arrivalStation = stationService.findById(target);
        List<Line> lines = lineService.getAllLines();

        Graph graph = new Graph(lines);

        List<StationResponse> stationResponses = graph.getShortestPath(startStation, arrivalStation)
                .stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        int distance = graph.getShortestDistance(startStation, arrivalStation);

        return new PathResponse(stationResponses, distance);
    }
}
