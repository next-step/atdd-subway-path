package nextstep.subway.path.domain.graph;

import nextstep.subway.path.domain.PathResult;
import nextstep.subway.station.domain.Station;

public interface Path {

    PathResult find(Station source, Station target);
}
