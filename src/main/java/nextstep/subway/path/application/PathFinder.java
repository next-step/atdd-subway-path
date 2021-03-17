package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;

import java.util.List;

public interface PathFinder {
    public void initialize(List< Line > lines);

    public PathResponse searchShortestPath(Station source, Station target);
}
