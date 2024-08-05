package nextstep.subway.path.application;

import java.util.List;
import nextstep.subway.line.domain.entity.Line;
import nextstep.subway.path.application.dto.PathResponse;
import nextstep.subway.station.domain.Station;

public interface PathFinder {
    PathResponse find(List<Line> lines, Station source, Station target);
}
