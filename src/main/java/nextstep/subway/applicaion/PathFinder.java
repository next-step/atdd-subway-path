package nextstep.subway.applicaion;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.SubwayPath;

import java.util.List;

public interface PathFinder {
    SubwayPath findPath(List<Line> lines, Station sourceStation, Station targetStation);
}
