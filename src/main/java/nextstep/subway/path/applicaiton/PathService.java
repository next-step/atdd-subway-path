package nextstep.subway.path.applicaiton;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import org.springframework.stereotype.Service;

import java.util.LinkedList;

@Service
public class PathService {

    public PathResponse getShortestPath(Long source, Long target) {
        PathFinder pathFinder = new PathFinder(new LinkedList<Line>());
        return pathFinder.getShortestPath(source, target);
    }
}
