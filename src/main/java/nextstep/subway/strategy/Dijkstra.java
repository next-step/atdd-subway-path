package nextstep.subway.strategy;

import nextstep.subway.domain.ShortestPathType;
import org.springframework.stereotype.Component;

import static nextstep.subway.domain.ShortestPathType.DIJKSTRA;

@Component
public class Dijkstra implements ShortestPathStrategy {

    @Override
    public int weight() {
        return 0;
    }

    @Override
    public boolean supported(ShortestPathType shortestPathType) {
        return shortestPathType == DIJKSTRA;
    }
}
