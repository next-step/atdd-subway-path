package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;

import java.util.List;

public interface PathFinder {
    Path find(List<Line> lines, Station source, Station target);
}
