package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.ShortestPath;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.SubwayMap;
import nextstep.subway.exception.CustomException;
import nextstep.subway.exception.code.StationCode;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PathService {
    private final StationRepository stationRepository;
    private final LineRepository lineRepository;

    public PathResponse getShortestPath(final Long source, final Long target) {
        Station sourceStation = findStation(source);
        Station targetStation = findStation(target);
        List<Line> lines = lineRepository.findAll();

        SubwayMap subwayMap = new SubwayMap(lines);
        ShortestPath shortestPath = subwayMap.getShortestPath(sourceStation, targetStation);
        return PathResponse.of(shortestPath);
    }

    private Station findStation(final Long stationId) {
        return stationRepository.findById(stationId)
                                .orElseThrow(() -> new CustomException(StationCode.STATION_NOT_FOUND));
    }
}
