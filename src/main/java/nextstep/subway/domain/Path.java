package nextstep.subway.domain;

import nextstep.subway.strategy.ShortestPathStrategy;

public class Path {

    private ShortestPathStrategy shortestPathStrategy;

    public Path(ShortestPathStrategy shortestPathStrategy) {
        this.shortestPathStrategy = shortestPathStrategy;
    }

}
