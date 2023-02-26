package nextstep.subway.domain;

public interface PathFinder {
    Path findShortestPath(Station source, Station target);
}
