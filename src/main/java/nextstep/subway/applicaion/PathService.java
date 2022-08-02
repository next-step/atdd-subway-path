package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.ShortestPathResult;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.StationCode;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PathService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;
    private final PathFinder pathFinder;

    public PathResponse getShortestPath(final Long source, final Long target) {
        Station sourceStation = findStation(source);
        Station targetStation = findStation(target);
        List<Line> lines = lineRepository.findAll();

        ShortestPathResult shortestPath = pathFinder.calShortestPath(lines, sourceStation, targetStation);
        return PathResponse.of(shortestPath);
    }

    private Station findStation(final Long stationId) {
        return stationRepository.findById(stationId)
                                .orElseThrow(() -> new CustomException(StationCode.STATION_NOT_FOUND));
    }
}
