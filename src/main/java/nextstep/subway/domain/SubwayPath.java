package nextstep.subway.domain;

import java.util.Collections;
import java.util.List;

import nextstep.subway.domain.sections.Sections;

public class SubwayPath {
    private final Sections sections;
    private final Station source;

    public SubwayPath(Sections sections, Station source) {
        this.sections = sections;
        this.source = source;
    }

    public List<Station> getStations() {
        List<Station> stations = sections.getStations();
        if (!stations.get(0).equals(source)) {
            Collections.reverse(stations);
        }

        return stations;
    }

    public int getTotalDistance() {
        return sections.getTotalDistance();
    }
}
