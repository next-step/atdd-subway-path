package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathStationResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, PathFinder pathFinder) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(Long sourceStationId, Long targetStationId) {
        List<Line> lines = this.lineRepository.findAll();
        List<LineStation> shortestPath = this.pathFinder.findShortestPath(lines, sourceStationId, targetStationId);
        return this.toPathResponse(shortestPath);
    }

    private PathResponse toPathResponse(List<LineStation> shortestPath) {
        List<Long> shortestPathStationIds = shortestPath.stream()
                .map(LineStation::getStationId)
                .collect(Collectors.toList());

        Map<Long, Station> shortestPathStations = this.stationRepository.findAllById(shortestPathStationIds).stream()
                .collect(Collectors.toMap(it -> it.getId(), it -> it));

        return this.toPathResponse(shortestPath, shortestPathStationIds, shortestPathStations);
    }

    private PathResponse toPathResponse(final List<LineStation> shortestPath, final List<Long> shortestPathStationIds, final Map<Long, Station> shortestPathStations) {
        List<PathStationResponse> pathStationResponses = shortestPathStationIds.stream()
                .map(it -> {
                    Station station = shortestPathStations.get(it);
                    return new PathStationResponse(station.getId(), station.getName(), station.getCreatedDate());
                })
                .collect(Collectors.toList());

        int distance = 0;
        int duration = 0;

        int loop = shortestPath.size();
        for (int i = 1; i < loop; i++) {
            LineStation lineStation = shortestPath.get(i);
            distance += lineStation.getDistance();
            duration += lineStation.getDuration();
        }

        return new PathResponse(pathStationResponses, distance, duration);
    }
}
