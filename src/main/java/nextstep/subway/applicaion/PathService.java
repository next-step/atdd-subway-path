package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import nextstep.subway.domain.exception.StationNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, PathFinder pathFinder) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(PathRequest request) {
        validatePathRequest(request);

        List<Line> lines = lineRepository.findAll();
        Station source = stationRepository.findById(request.getSource()).orElseThrow(() -> new StationNotFoundException());
        Station target = stationRepository.findById(request.getTarget()).orElseThrow(() -> new StationNotFoundException());

        List<Station> path = pathFinder.findPath(lines, source, target);
        int pathWeight = pathFinder.findPathWeight(lines, source, target);

        return createPathResponse(path, pathWeight);
    }

    private static void validatePathRequest(PathRequest request) {
        if (Objects.isNull(request.getTarget()) || Objects.isNull(request.getSource())) {
            throw new StationNotFoundException();
        }

        if (request.getTarget() == request.getSource()) {
            throw new DataIntegrityViolationException("출발역과 도착역이 같습니다.");
        }
    }

    private PathResponse createPathResponse(List<Station> path, int pathWeight) {
        List<StationResponse> paths = path.stream()
                .map(it -> createStationResponse(it))
                .collect(Collectors.toList());
        return new PathResponse(paths, pathWeight);
    }

    public StationResponse createStationResponse(Station station) {
        return new StationResponse(
                station.getId()
                , station.getName()
        );
    }
}
