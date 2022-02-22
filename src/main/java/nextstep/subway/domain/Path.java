package nextstep.subway.domain;

import java.util.List;

public class Path {
    private final Sections sections;

    public Path(Sections sections) {
        this.sections = sections;
    }

    public List<Station> stations() {
        return sections.getStationList();
    }

    public int totalDistance() {
        return sections.totalDistance();
    }
}
