package nextstep.subway.strategy;

import nextstep.subway.domain.Station;

import java.util.List;

public interface ShortestPathStrategy {
    List<Station> findShortestPath(Station source, Station target);
    int findShortestDistance(Station source, Station target);
}
