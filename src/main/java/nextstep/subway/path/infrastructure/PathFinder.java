package nextstep.subway.path.infrastructure;

import java.util.List;

import nextstep.subway.line.domain.Section;
import nextstep.subway.path.infrastructure.dto.StationPaths;
import nextstep.subway.station.domain.Station;

public interface PathFinder {
    StationPaths findShortestPaths(List<Section> sections, Station source, Station target);
}
