package nextstep.subway.domain.strategy.remove;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionCollection;
import nextstep.subway.domain.Station;

public interface RemoveSectionStrategy {
    void removeSection(SectionCollection sectionCollection, Station station);
}
