package subway.path.component;

import subway.line.model.Section;
import subway.path.model.Path;
import subway.station.model.Station;

import java.util.List;

public interface PathFinder {
    Path findPath(List<Section> sections, Station sourceStation, Station targetStation);
}
