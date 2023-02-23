package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PathService {

    private final LineRepository lineRepository;
    private final StationService stationService;

    public PathResponse findShortestPath(final long sourceStationId, final long targetStationId) {
        final Station sourceStation = stationService.findById(sourceStationId);
        final Station targetStation = stationService.findById(targetStationId);

        final Path path = new Path(lineRepository);
        final List<Station> shortestPath = path.findShortestPath(sourceStation, targetStation);
        final int distance = path.findShortestPathDistance(sourceStation, targetStation);

        final List<StationResponse> stationResponses = shortestPath.stream()
                .map(StationResponse::new)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, distance);
    }
}
