package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;

public interface PathRepository {
    Path findShortestPath(Station source, Station target);
}
