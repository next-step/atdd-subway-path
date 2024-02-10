package nextstep.subway.domain;

import nextstep.subway.application.dto.PathResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PathFinder {
    public PathResponse findPath(final List<Line> lines, final Station sourceStation, final Station targetStation) {
        throw new UnsupportedOperationException("Unsupported findPath");
    }
}
