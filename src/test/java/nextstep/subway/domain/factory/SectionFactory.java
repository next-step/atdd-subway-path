package nextstep.subway.domain.factory;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;

public class SectionFactory {

    public static final Section createSection(Station up, Station down, int distance) {
        return new Section(null, up, down, distance);
    }
}
