package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.PathFinder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;
    private StationService stationService;

    public PathService(LineRepository lineRepository, StationService stationService) {
        this.lineRepository = lineRepository;
        this.stationService = stationService;
    }

    public PathResponse shortPath(Long source, Long target) {
        Station startStation = stationService.findById(source);
        Station endStation = stationService.findById(target);

        PathFinder pathFinder = new PathFinder(lineRepository.findAll());

        List<Station> shortPathStations = pathFinder.shortPathStations(startStation, endStation);
        int pathWeight = pathFinder.shortPathWeight(startStation, endStation);

        return createPathResponse(shortPathStations, pathWeight);
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
