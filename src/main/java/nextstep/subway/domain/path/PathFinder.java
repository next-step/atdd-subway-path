package nextstep.subway.domain.path;

import java.util.List;
import nextstep.subway.domain.line.Line;
import nextstep.subway.domain.station.Station;

public interface PathFinder {

    void makeGraph(List<Line> lines);

    void addVerticesAndEdgesOf(Line line);

    ShortestPath executeDijkstra(Station sourceStation, Station targetStation);

    List<ShortestPath> executeKShortest(Station sourceStation, Station targetStation);

    void validateSourceTarget(Station source, Station target);

}
