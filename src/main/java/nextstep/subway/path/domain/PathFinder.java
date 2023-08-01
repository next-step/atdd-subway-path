package nextstep.subway.path.domain;

import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.entity.Station;

import java.util.Set;

public interface PathFinder {

    Sections findPath(Station source, Station target);

    Set<Station> getVertexSet();
}
