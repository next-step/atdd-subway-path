package nextstep.subway.applicaion;

import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PathService {

    private final StationRepository stationRepository;
    private final PathFinder pathFinder;

    public PathResponse findShortestPath(Long source, Long target) {
        var sourceStation = stationRepository.findById(source)
                .orElseThrow(IllegalArgumentException::new);
        var targetStation = stationRepository.findById(target)
                .orElseThrow(IllegalArgumentException::new);

        var path = pathFinder.solve(sourceStation, targetStation);

        return new PathResponse(
                path.getStations().stream().map(this::createStationResponse).collect(Collectors.toList()),
                path.getDistance()
        );
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }


}
