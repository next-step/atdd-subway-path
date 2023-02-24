package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public interface PathFinder {
    void init(List<Line> lines);

    PathResponse find(Station source, Station target);
}
