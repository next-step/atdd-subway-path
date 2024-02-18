package nextstep.subway.strategy;

import nextstep.subway.domain.entity.Path;
import nextstep.subway.domain.entity.Station;

public interface PathStrategy {
    public Path findShortestPath(Station source, Station target);
}
