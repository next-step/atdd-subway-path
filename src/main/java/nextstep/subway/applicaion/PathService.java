package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import nextstep.subway.domain.exception.StationNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
    private final LineRepository lineRepository;
    private final StationRepository stationRepository;
    private final PathFinder pathFinder;

    public PathService(LineRepository lineRepository, StationRepository stationRepository, PathFinder pathFinder) {
        this.stationRepository = stationRepository;
        this.pathFinder = pathFinder;
        this.lineRepository = lineRepository;
    }

    public PathResponse findPath(PathRequest request) {
        Station source = stationRepository.findById(request.getSource()).orElseThrow(() -> new StationNotFoundException());
        Station target = stationRepository.findById(request.getTarget()).orElseThrow(() -> new StationNotFoundException());
        pathFinder.init(lineRepository.findAll());

        List<Station> path = pathFinder.findPath(source, target);
        int pathWeight = pathFinder.findPathWeight(source, target);

        return createPathResponse(path, pathWeight);
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
