package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.exception.PathSameStationException;
import nextstep.subway.exception.StationNotExistException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PathService {
    private final PathFinder pathFinder;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathResponse getPath(long srcStationId, long dstStationId) {
        validateIsNotSameStation(srcStationId, dstStationId);
        Station srcStation = stationRepository.findById(srcStationId).orElseThrow(StationNotExistException::new);
        Station dstStation = stationRepository.findById(dstStationId).orElseThrow(StationNotExistException::new);
        List<Line> allLine = lineRepository.findAll();
        PathResult path = pathFinder.findPath(allLine, srcStation, dstStation);
        List<StationResponse> stationResponses = path.getStations().stream()
                .map(StationResponse::from)
                .collect(Collectors.toList());
        return new PathResponse(stationResponses, path.getWeight());
    }

    private static void validateIsNotSameStation(long srcStationId, long dstStationId) {
        if (srcStationId == dstStationId) {
            throw new PathSameStationException();
        }
    }
}
