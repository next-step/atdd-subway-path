package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.DijkstraPathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
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

    public PathResponse getShortPath(Long source, Long target) {
        if(source.equals(target)) {
            throw new CustomException(CustomException.SAME_STATION_CAN_NOT_SEARCH_PATH);
        }

        List<Line> lines = lineService.getLines();
        DijkstraPathFinder pathFinder = new DijkstraPathFinder(lines);
        // <-> KshortestPathFinder.class

        GraphPath<Long, String> shotestGraph = pathFinder.getShortestPath(source, target);

        List<StationResponse> stations = shotestGraph.getVertexList().stream()
                .map(s -> stationService.findById(s))
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return PathResponse.of(stations, (int)shotestGraph.getWeight());
    }
}
