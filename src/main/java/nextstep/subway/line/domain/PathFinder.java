package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.exception.EqualStationException;
import nextstep.subway.station.exception.NoStationException;
import org.jgrapht.GraphPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class PathFinder {

    private List<Line> lines;

    public PathFinder(List<Line> lines) {
        this.lines = Collections.unmodifiableList(lines);
    }

    public StationGraph getPathInfo(Station source, Station target) {
        validateNullStation(source, target);
        validateEqualStation(source, target);
        SubwayMap subWayMap = new SubwayMap(new WeightedMultigraph(DefaultWeightedEdge.class));
        GraphPath pathInfo = subWayMap.getPath(lines, source, target);
        return new StationGraph(pathInfo);
    }

    private void validateNullStation(Station source, Station target) {
        if (Objects.isNull(source) || Objects.isNull(target)) {
            throw new NoStationException();
        }
    }

    private void validateEqualStation(Station source, Station target) {
        if (source == target) {
            throw new EqualStationException();
        }
    }
}
