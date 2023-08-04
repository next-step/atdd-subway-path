package nextstep.subway.domain;

public interface PathFinderStrategy {

  Path findShortestPath(Station source, Station target);

}
