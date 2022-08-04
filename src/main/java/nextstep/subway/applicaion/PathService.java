package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(LineService lineService, StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse getPath(Long source, Long target) {
        final Station upStation = stationService.findById(source, true);
        final Station downStation = stationService.findById(target, false);

        final List<Line> lines = lineService.findAllByStationIdIn(Arrays.asList(source, target));

        final Path path = Path.of(lines, upStation, downStation);
        final List<Station> stations = path.getShortestPath();
        final List<StationResponse> stationResponses = createStationResponse(stations);

        return new PathResponse(stationResponses, path.getWeight());
    }

    private List<StationResponse> createStationResponse(List<Station> stations) {
        return stations.stream()
                .map(stationService::createStationResponse)
                .collect(Collectors.toList());
    }
}
