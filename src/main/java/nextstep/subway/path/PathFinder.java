package nextstep.subway.path;

import nextstep.subway.line.section.Lines;
import nextstep.subway.station.Station;

public interface PathFinder {
    Path shortcut(Lines lines,
                  Station source,
                  Station target);
}
