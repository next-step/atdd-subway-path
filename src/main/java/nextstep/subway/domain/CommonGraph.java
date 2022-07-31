package nextstep.subway.domain;

import java.util.List;

public interface CommonGraph {

    public List<Station> getShortestPath(Station source, Station target);

    public int getShortestDistance(Station source, Station target);

}
