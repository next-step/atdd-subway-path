package nextstep.subway.applicaion;

import nextstep.subway.applicaion.dto.PathResult;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public interface PathFinder {
    PathResult findPath(List<Line> allLines, Station srcStation, Station dstStation);
}
