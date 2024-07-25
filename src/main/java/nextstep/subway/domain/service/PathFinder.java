package nextstep.subway.domain.service;

import nextstep.subway.domain.model.Path;
import nextstep.subway.domain.model.Station;

public interface PathFinder {
    Path findPath(Station source, Station target);
}
