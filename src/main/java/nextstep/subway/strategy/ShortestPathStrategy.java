package nextstep.subway.strategy;

import nextstep.subway.domain.ShortestPathType;

public interface ShortestPathStrategy {
    int weight();
    boolean supported(ShortestPathType shortestPathType);
}
