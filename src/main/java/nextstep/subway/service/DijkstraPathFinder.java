package nextstep.subway.service;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.path.Path;
import nextstep.subway.domain.path.PathFinderStrategy;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DijkstraPathFinder implements PathFinderStrategy {
    @Override
    public Path findShortestPathAndItsDistance(List<Line> lines, Station sourceStation, Station targetStation) {
        return null;
    }
}
