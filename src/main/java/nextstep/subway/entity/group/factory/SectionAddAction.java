package nextstep.subway.entity.group.factory;

import nextstep.subway.entity.Line;
import nextstep.subway.entity.Section;
import nextstep.subway.entity.Station;
import nextstep.subway.entity.group.SectionGroup;

public interface SectionAddAction {

    default Section add(Line line, Station upStation, Station downStation, int distance,
        Section nowSection, SectionGroup sectionGroup) {

        validate(upStation, downStation, distance, nowSection, sectionGroup);
        return addAction(line, upStation, downStation, distance, nowSection, sectionGroup);
    }

    void validate(Station upStation, Station downStation, int distance, Section section, SectionGroup sectionGroup);

    Section addAction(Line line, Station upStation, Station downStation, int distance,
        Section nowSection, SectionGroup sectionGroup);

    boolean isAdd();
}
