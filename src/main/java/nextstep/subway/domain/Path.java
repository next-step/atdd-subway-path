package nextstep.subway.domain;

import nextstep.subway.exception.ApplicationException;
import nextstep.subway.strategy.ShortestPathStrategy;

import java.util.ArrayList;
import java.util.List;

public class Path {

    private ShortestPathStrategy shortestPathStrategy;

    public Path(ShortestPathStrategy shortestPathStrategy) {
        this.shortestPathStrategy = shortestPathStrategy;
    }

    public void validatePath(Station source, Station target) {
        if (source.equals(target)) {
            throw new ApplicationException("출발역과 도착역이 같은 경우 경로를 조회할 수 없습니다.");
        } else {
            throw new ApplicationException("출발역과 도착역이 연결되어 있지 않습니다.");
        }
    }

    public List<Station> findShortenStations() {
        return new ArrayList<>();
    }

    public int calculateShortenDistance() {
        return 0;
    }
}
