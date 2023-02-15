package nextstep.subway.domain.strategy.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.SectionCollection;

public class BasicAddSection implements AddSectionStrategy {

    @Override
    public void addSection(SectionCollection sectionCollection, Section section) {
        sectionCollection.addSection(section);
    }
}
