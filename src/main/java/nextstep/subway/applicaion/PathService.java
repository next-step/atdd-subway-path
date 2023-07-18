package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PathService {
    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findPath(PathRequest request) {
        if (request.getTarget() == request.getSource()) {
            throw new DataIntegrityViolationException("출발역과 도착역이 같습니다.");
        }

        List<Line> lines = lineRepository.findAll();

        Station source = stationRepository.findById(request.getSource())
                .orElseThrow(() -> new DataIntegrityViolationException("존재하지 않는 역입니다."));
        Station target = stationRepository.findById(request.getTarget())
                .orElseThrow(() -> new DataIntegrityViolationException("존재하지 않는 역입니다."));

        List<Station> path = PathFinder.findPath(lines, source, target);
        int pathWeight = PathFinder.findPathWeight(lines, source, target);

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
