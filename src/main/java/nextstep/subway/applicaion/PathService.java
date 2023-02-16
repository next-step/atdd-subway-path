package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {
    private final PathFinder shortestPathService;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathResponse getPath(long srcStationId, long dstStationId) {
        Station srcStation = stationRepository.findById(srcStationId).orElseThrow(IllegalArgumentException::new);
        Station dstStation = stationRepository.findById(dstStationId).orElseThrow(IllegalArgumentException::new);
        List<Line> allLine = lineRepository.findAll();
        PathResult path = shortestPathService.findPath(allLine, srcStation, dstStation);
        List<StationResponse> stationResponses = path.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, path.getWeight());
    }
}
