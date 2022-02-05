package nextstep.subway.domain.path;

import java.util.List;
import nextstep.subway.domain.station.Station;

public interface PathFinder {

    ShortestPath executeDijkstra(Station sourceStation, Station targetStation);

    List<ShortestPath> executeKShortest(Station sourceStation, Station targetStation);

}
