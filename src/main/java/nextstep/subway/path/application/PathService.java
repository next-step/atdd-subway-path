package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.StationRepository;
import nextstep.subway.station.dto.StationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PathService {

    private final LineRepository lineRepository;
    private final StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(long sourceStationId, long destinationStationId) {
        final List<Line> lines = lineRepository.findAll();
        final PathFinder pathFinder = new PathFinder(lines);
        final List<Long> stationIds = pathFinder.getShortestPath(sourceStationId, destinationStationId);
        final List<StationResponse> stationResponses = stationIds.stream()
                .map(stationRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(StationResponse::of)
                .collect(Collectors.toList());
        final double distance = pathFinder.getShortestPathWeight(sourceStationId, destinationStationId);
        return new PathResponse(stationResponses, (int) distance, 0);
    }
}
