package nextstep.subway.path;

import nextstep.subway.line.domain.Sections;
import nextstep.subway.station.domain.Station;

public interface ShortestPathFinder {
    ShortestPath find(Sections sections, Station sourceStation, Station targetStation);
}
