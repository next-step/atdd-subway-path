package nextstep.subway.domain;

import java.util.List;

public interface GraphStrategy {
    List<Station> findShortestPath(Station upStation, Station downStation);
    int getShortestPathSize();
}
