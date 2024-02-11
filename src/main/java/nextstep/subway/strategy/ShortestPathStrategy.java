package nextstep.subway.strategy;

import nextstep.subway.domain.ShortestPathType;
import nextstep.subway.domain.Station;

import java.util.List;

public interface ShortestPathStrategy {
    List<Station> findShortestStationPath(Station source, Station target);
    int totalPathDistance(Station source, Station target);
    boolean support(ShortestPathType shortestPathType);
}
