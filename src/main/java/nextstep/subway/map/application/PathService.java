package nextstep.subway.map.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.map.domain.PathFinder;
import nextstep.subway.map.dto.PathAssembler;
import nextstep.subway.map.dto.PathRequest;
import nextstep.subway.map.dto.PathResponse;
import nextstep.subway.map.exception.NonExistSourceOrTargetException;
import nextstep.subway.map.exception.NotConnectedSourceAndTargetException;
import nextstep.subway.map.exception.SameSourceAndTagetException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final PathFinder pathFinder;
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(PathFinder pathFinder, LineRepository lineRepository, StationRepository stationRepository) {
        this.pathFinder = pathFinder;
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(PathRequest request) {
        checkPath(request);
        List<Line> lines = lineRepository.findAll();

        List<LineStation> shortestPath = pathFinder.findShortestPath(lines, request);

        if (shortestPath.isEmpty()) {
            throw new NotConnectedSourceAndTargetException();
        }

        List<Long> stationIds = shortestPath.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());
        List<Station> stations = stationRepository.findAllById(stationIds);

        return PathAssembler.toPathResponse(shortestPath, stations);
    }

    private void checkPath(PathRequest request) {
        if (isSameSourceAndTarget(request)) {
            throw new SameSourceAndTagetException();
        }

        if (!isExistStation(request)){
            throw new NonExistSourceOrTargetException();
        }
    }

    private boolean isSameSourceAndTarget(PathRequest request) {
        return request.getSource().equals(request.getTarget());
    }

    private boolean isExistStation(PathRequest request) {
        return stationRepository.existsById(request.getSource()) && stationRepository.existsById(request.getTarget());
    }
}
