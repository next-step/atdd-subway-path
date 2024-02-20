package nextstep.subway.domain.path;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public interface PathFinderStrategy {
    public Path findShortestPathAndItsDistance(List<Line> lines, Station sourceStation, Station targetStation);
}
