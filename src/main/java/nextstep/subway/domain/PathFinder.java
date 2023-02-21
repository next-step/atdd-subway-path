package nextstep.subway.domain;

import nextstep.subway.domain.dto.PathResponse;

import java.util.List;

public interface PathFinder {

    void init(List<Line> lines);

    PathResponse find(Station source, Station target);
}
