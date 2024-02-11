package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;
import nextstep.subway.strategy.ShortestPathStrategy;

import java.util.List;

public class Path {

    private ShortestPathStrategy shortestPathStrategy;

    public Path(ShortestPathStrategy shortestPathStrategy) {
        this.shortestPathStrategy = shortestPathStrategy;
    }

    public List<Station> findShortestPath(Station source, Station target) {
        validateIsSameBy(source, target);
        return shortestPathStrategy.findShortestPath(source, target);
    }

    private void validateIsSameBy(Station source, Station target) {
        if (source.equals(target)) {
            throw new ApplicationException("출발역과 도착역이 같은 경우 경로를 조회할 수 없습니다.");
        }
    }

    public int findShortestDistance(Station source, Station target) {
        return shortestPathStrategy.findShortestDistance(source, target);
    }

}
