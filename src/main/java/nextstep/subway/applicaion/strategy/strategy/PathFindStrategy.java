package nextstep.subway.applicaion.strategy.strategy;

import nextstep.subway.domain.Station;

import java.util.List;

public interface PathFindStrategy {

    List<Station> findShortPath(Station source, Station target);
    int getShortestDistance(Station source, Station target);


}
