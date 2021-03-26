package nextstep.subway.path.domain.graph;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.List;

public interface Graph {

    Path getPath(List<Station> stations, List<Section> sections);
}
