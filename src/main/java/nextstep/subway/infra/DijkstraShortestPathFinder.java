package nextstep.subway.infra;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.PathFinder;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.dto.PathResponse;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.WeightedMultigraph;
import org.springframework.stereotype.Component;

import java.util.List;

import static nextstep.subway.common.constants.ErrorConstant.NOT_FOUND_STATION;
import static nextstep.subway.common.constants.ErrorConstant.SAME_STATION;

@Component
public class DijkstraShortestPathFinder implements PathFinder {

    @Override
    public void init(List<Line> lines) {

    }

    @Override
    public PathResponse find(Station source, Station target) {
        if (source.equals(target)) {
            throw new IllegalArgumentException(SAME_STATION);
        }

        return null;
    }
}
