package nextstep.subway.applicaion;

import lombok.RequiredArgsConstructor;
import nextstep.subway.applicaion.dto.PathRequest;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.LineRepository;
import nextstep.subway.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class PathService {

    private final StationService stationService;
    private final LineRepository lineRepository;
    private final PathFinder pathFinder;


    @Transactional(readOnly = true)
    public PathResponse getShortestPath(final PathRequest pathRequest) {
        pathRequest.validate();

        final List<Line> lines = lineRepository.findAll();
        final Station source = stationService.findById(pathRequest.getSource());
        final Station target = stationService.findById(pathRequest.getTarget());

        return pathFinder.findPath(lines, source, target);
    }
}
