package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.LineStation;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.exception.SameSourceAndTagetException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {
    private final PathFinder pathFinder;
    private final LineRepository lineRepository;

    public PathService(PathFinder pathFinder, LineRepository lineRepository) {
        this.pathFinder = pathFinder;
        this.lineRepository = lineRepository;
    }

    public PathResponse findShortestPath(PathRequest request) {
        checkPath(request);
        List<Line> lines = lineRepository.findAll();
        List<LineStation> lineStations = pathFinder.findShortestPath(lines, request.getSource(),request.getTarget());
        return null;
    }

    private void checkPath(PathRequest request) {
        if (isSameSourceAndTarget(request)) {
            throw new SameSourceAndTagetException();
        }
    }

    private boolean isSameSourceAndTarget(PathRequest request) {
        return request.getSource().equals(request.getTarget());
    }
}
