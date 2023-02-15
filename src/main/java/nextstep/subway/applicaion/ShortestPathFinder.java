package nextstep.subway.applicaion;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.PathResult;
import org.springframework.stereotype.Component;

@Component
public interface ShortestPathFinder {

    PathResult findRoute(final Path path);
}
