package nextstep.subway.line.service;

import lombok.RequiredArgsConstructor;
import nextstep.subway.line.algorithm.ShortestPathFinder;
import nextstep.subway.line.dto.ShortestPathResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.LineRepository;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.exception.StationNotFoundException;
import nextstep.subway.station.dto.StationResponse;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.entity.StationRepository;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
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

        ShortestPathFinder pathFinder = new ShortestPathFinder();
        List<Station> searchedPath = pathFinder.searchPath(lineList, sourceStation, targetStation);
        BigInteger weight = pathFinder.getShortestWeight(lineList, sourceStation, targetStation);
        return new ShortestPathResponse(searchedPath.stream().map(StationResponse::from).collect(Collectors.toList()), weight);
    }

    private Station getStation(long sourceStationId) {
        return stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new StationNotFoundException("station.not.found"));
    }
}
