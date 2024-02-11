package nextstep.subway.path;

import nextstep.subway.line.section.Sections;

public class Path {
    private final Sections sections;
    private final Long distance;

    public Path(Sections sections,
                Long distance) {
        this.sections = sections;
        this.distance = distance;
    }

    public Sections getSections() {
        return sections;
    }

    public Long getDistance() {
        return distance;
    }
}
