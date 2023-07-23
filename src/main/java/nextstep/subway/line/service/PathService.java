package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.domain.PathFinder;
import nextstep.subway.line.domain.ShortestPathFinder;
import nextstep.subway.line.dto.ShortestPathResponse;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.line.domain.entity.LineRepository;
import nextstep.subway.line.exception.StationNotFoundException;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.entity.StationRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public ShortestPathResponse getShortestPath(long sourceStationId, long targetStationId) {

        Station sourceStation = getStation(sourceStationId);
        Station targetStation = getStation(targetStationId);

        List<Line> lineList = lineRepository.findAll();

        PathFinder pathFinder = new ShortestPathFinder(lineList, sourceStation, targetStation);
        List<Station> searchedPath = pathFinder.getPath();
        BigInteger weight = pathFinder.getWeight();
        return new ShortestPathResponse(searchedPath.stream().map(StationResponse::from).collect(Collectors.toList()), weight);
    }

    private Station getStation(long sourceStationId) {
        return stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new StationNotFoundException("station.not.found"));
    }
}
