package nextstep.subway.application;

import nextstep.subway.application.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PathService {
    private final LineService lineService;
    private final PathFinder pathFinder;

    public PathService(final LineService lineService, final PathFinder pathFinder) {
        this.lineService = lineService;
        this.pathFinder = pathFinder;
    }

    public PathResponse findPath(final Long source, final Long target) {
        final List<Line> lines = lineService.findAllLine();

        return pathFinder.findPath(lines, source, target);
    }
}
