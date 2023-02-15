package nextstep.subway.domain;

import nextstep.subway.applicaion.dto.PathResponse;

import java.util.List;

public interface PathFinder {
    PathResponse findPath(List<Line> line, Station source, Station target);
}
