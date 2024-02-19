package nextstep.subway.application;

import nextstep.subway.application.dto.PathResult;
import nextstep.subway.entity.Line;
import nextstep.subway.entity.Station;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PathFinder {
    public PathResult findShortestPath(List<Line> lines, Station departureStation, Station arrivalStation) {
        return new PathResult(new ArrayList<>(), 0);
    }
}
