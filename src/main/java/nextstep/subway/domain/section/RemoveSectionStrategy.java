package nextstep.subway.domain.section;

import nextstep.subway.domain.Station;

public interface RemoveSectionStrategy {
    void removeSection(SectionCollection sectionCollection, Station station);
}
