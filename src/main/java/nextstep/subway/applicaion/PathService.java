package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import org.springframework.stereotype.Service;

@Service
public class PathService {

    private final PathFinder pathFinder;

    public PathService(PathFinder pathFinder) {
        this.pathFinder = pathFinder;
    }

    public PathResponse find(Long sourceId, Long targetId) {
        return pathFinder.find(sourceId, targetId);
    }
}
