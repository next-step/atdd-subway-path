package nextstep.subway.line;

import java.util.List;
import nextstep.subway.station.Station;

public interface PathFinder {

    PathResponse findShortestDistance(Station sourceStation, Station targetStation, List<Line> allStation);
}
