package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.NonExistSourceOrTargetException;
import nextstep.subway.path.exception.NotConnectedSourceAndTargetException;
import nextstep.subway.path.exception.SameSourceAndTagetException;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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

        List<LineStation> shortestPath = pathFinder.findShortestPath(lines, request.getSource(),request.getTarget());

        if (shortestPath.isEmpty()) {
            throw new NotConnectedSourceAndTargetException();
        }

        return toPathResponse(shortestPath);
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

    private PathResponse toPathResponse(List<LineStation> shortestPath) {
        List<Long> stationIds = shortestPath.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        Integer distance = sumDistance(shortestPath);
        Integer duration = sumDuration(shortestPath);


        List<StationResponse> stationResponses = getStationResponses(shortestPath, stationIds);

        return new PathResponse(stationResponses, distance, duration);
    }

    private List<StationResponse> getStationResponses(List<LineStation> shortestPath, List<Long> stationIds) {
        List<Station> stations = stationRepository.findAllById(stationIds);

        return shortestPath.stream()
                    .map(it -> {
                        Optional<Station> station = stations.stream()
                                .filter(st -> st.getId() == it.getStationId())
                                .findFirst();
                        return StationResponse.of(station.orElseThrow(RuntimeException::new));
                    })
                    .collect(Collectors.toList());
    }

    private Integer sumDuration(List<LineStation> shortestPath) {
        return shortestPath.stream()
                    .mapToInt(it -> it.getDuration())
                    .sum();
    }

    private Integer sumDistance(List<LineStation> shortestPath) {
        return shortestPath.stream()
                    .mapToInt(it -> it.getDistance())
                    .sum();
    }
}
