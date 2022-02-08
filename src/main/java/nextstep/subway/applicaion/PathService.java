package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.error.exception.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathService(StationRepository stationRepository, LineRepository lineRepository) {
        this.stationRepository = stationRepository;
        this.lineRepository = lineRepository;
    }

    public PathResponse searchShortestPaths(Long sourceId, Long targetId) {
        Station source = findStation(sourceId);
        Station target = findStation(targetId);

        PathFinder pathFinder = new PathFinder(lineRepository.findAll());
        List<Station> shortestPath = pathFinder.getShortestPaths(source, target);
        int totalDistance = pathFinder.getPathDistance(source, target);

        return new PathResponse(createStationResponses(shortestPath), totalDistance);
    }

    private Station findStation(Long stationId) {
        return stationRepository.findById(stationId)
                .orElseThrow(() -> new EntityNotFoundException(stationId));
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream().map(StationResponse::of).collect(Collectors.toList());
    }
}
