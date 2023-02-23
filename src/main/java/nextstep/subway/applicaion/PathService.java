package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.DijkstraPathFinder;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.exception.CustomException;
import org.jgrapht.GraphPath;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
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
        validStations(source, target);

        List<Line> lines = lineService.getLines();
        PathFinder pathFinder = new DijkstraPathFinder(lines);
        // <-> KshortestPathFinder.class

        GraphPath<Long, String> shotestGraph = pathFinder.getPath(source, target);

        List<StationResponse> stations = shotestGraph.getVertexList().stream()
                .map(s -> stationService.findById(s))
                .map(StationResponse::of)
                .collect(Collectors.toList());

        return PathResponse.of(stations, (int)shotestGraph.getWeight());
    }

    private void validStations(Long source, Long target) {
        if(source.equals(target)) {
            throw new CustomException(CustomException.SAME_STATION_CAN_NOT_SEARCH_PATH);
        }

        try {
            stationService.findById(source);
            stationService.findById(target);
        } catch (IllegalArgumentException e) {
            throw new CustomException(CustomException.PATH_MUST_CONTAIN_STATION);
        }
    }
}
