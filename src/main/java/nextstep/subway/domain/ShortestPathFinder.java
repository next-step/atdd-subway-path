package nextstep.subway.domain;

import java.util.List;

public interface ShortestPathFinder {

    Path findShortestPath(List<Line> lines, Long source, Long target);
}
