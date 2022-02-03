package nextstep.subway.line.infrastructure;

import java.util.List;

import nextstep.subway.line.domain.Section;
import nextstep.subway.line.domain.Sections;

public interface PathFinder {
    Sections findShortestPaths(List<Section> sections);
}
