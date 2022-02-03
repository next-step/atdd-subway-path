package nextstep.subway.line.infrastructure;

import java.util.List;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.infrastructure.dto.StationPath;
import nextstep.subway.station.domain.Station;

public interface PathFinder {
    StationPath findShortestPaths(List<Section> sections, Station source, Station target);
}
