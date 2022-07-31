package nextstep.subway.applicaion.facade;

import nextstep.subway.domain.Station;
import nextstep.subway.applicaion.strategy.strategy.PathFindStrategy;

import java.util.List;


public class PathFinder {
    private final PathFindStrategy pathFindStrategy;

    public PathFinder(PathFindStrategy dijkstraPathFindStrategy) {
        this.pathFindStrategy = dijkstraPathFindStrategy;
    }

    public List<Station> findShortestPath(Station source, Station target) {
        return pathFindStrategy.findShortPath(source, target);
    }

    public int getShortestDistance(Station source, Station target) {
        return pathFindStrategy.getShortestDistance(source, target);
    }

}
