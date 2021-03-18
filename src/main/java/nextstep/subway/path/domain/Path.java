package nextstep.subway.path.domain;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Path {

    private List<Section> sections;
    private Set<Station> stations;

    public Path() {
    }

    public Path(List<Section> sections) {
        this.sections = sections;
    }

    public List<Station> findShortestPath(Station source, Station target) {

        return new ArrayList<>();
    }

    public int getTotalDistance() {
        return 0;
    }
}
