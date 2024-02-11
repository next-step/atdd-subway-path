package nextstep.subway.path;

import nextstep.subway.line.Line;
import nextstep.subway.station.Station;

import java.util.List;

public interface PathFinder {
    Path shortcut(List<Line> lines,
                  Station source,
                  Station target);
}
