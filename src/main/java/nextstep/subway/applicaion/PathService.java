package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.utils.PathFinder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private static final String SAME_STATION_ERROR_MESSAGE = "출발역과 도착역이 동일합니다.";
    private LineRepository lineRepository;
    private StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse shortPath(Long source, Long target) {
        validationShortPath(source, target);
        Station startStation = stationService.findById(source);
        Station endStation = stationService.findById(target);

        List<Line> lines = lineRepository.findAll();
        PathFinder pathFinder = new PathFinder(lines);

        return createPathResponse(pathFinder.getShortPath(startStation, endStation), pathFinder.getShortPathWeight(startStation, endStation));
    }

    private void validationShortPath(Long source, Long target) {
        if (source == target) {
            throw new IllegalArgumentException(SAME_STATION_ERROR_MESSAGE);
        }
    }

    private PathResponse createPathResponse(List<Station> stations, int shortPathWeight) {
        return new PathResponse(
                createStationResponses(stations),
                shortPathWeight
        );
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
                .map(it -> stationService.createStationResponse(it))
                .collect(Collectors.toList());
    }
}
