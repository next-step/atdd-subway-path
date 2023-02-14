package nextstep.subway.domain.strategy.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionCollection;

public interface AddSectionStrategy {
    void addSection(SectionCollection sectionCollection, Section section);
}
