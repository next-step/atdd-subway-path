package nextstep.subway.infra;

import nextstep.subway.applicaion.PathFinder;
import nextstep.subway.applicaion.dto.PathResponse;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

import java.util.List;

public class DijkstraShortestPath implements PathFinder {
    @Override
    public void init(List<Line> lines) {
        
    }

    @Override
    public PathResponse find(Station source, Station target) {
        return null;
    }
}
