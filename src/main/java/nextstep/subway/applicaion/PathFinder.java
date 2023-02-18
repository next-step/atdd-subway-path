package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public interface PathFinder {
    PathResponse findPath(final List<Line> lines, final Station source, final Station target);
}
