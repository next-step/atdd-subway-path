package nextstep.subway.common.fixture;

import nextstep.subway.line.domain.Section;
import nextstep.subway.station.domain.Station;
import nextstep.util.ReflectionUtils;

public class SectionFactory {
    private SectionFactory() {

    }

    public static Section createSection(final Station upStation, final Station downStation, final int distance) {
        return new Section(upStation, downStation, distance);
    }

    public static Section createSection(final Long id, final Station upStation, final Station downStation, final int distance) {
        final Section section = createSection(upStation, downStation, distance);
        ReflectionUtils.injectIdField(section, id);
        return section;
    }

}
