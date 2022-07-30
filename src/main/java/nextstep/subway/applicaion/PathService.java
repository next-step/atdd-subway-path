package nextstep.subway.applicaion;

import java.util.List;
import java.util.stream.Collectors;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import nextstep.subway.exception.SameStationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PathService {

    private final LineService lineService;
    private final StationService stationService;

    public PathService(final LineService lineService, final StationService stationService) {
        this.lineService = lineService;
        this.stationService = stationService;
    }

    public PathResponse getShortestPath(final long source, final long target) {

        final Station startStation = stationService.findById(source);
        final Station endStation = stationService.findById(target);

        if (startStation.equals(endStation)) {
            throw new SameStationException();
        }

        final List<Line> lines = lineService.lineContainingStation(List.of(startStation.getId(), endStation.getId()));

        final Path path = new Path(lines, startStation, endStation);
        final List<Station> shortestStations = path.getShortestPath();

        final List<StationResponse> stationResponses = createStationResponses(shortestStations);
        return new PathResponse(stationResponses, path.getTotalDistance());
    }

    private List<StationResponse> createStationResponses(final List<Station> stations) {
        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

}
