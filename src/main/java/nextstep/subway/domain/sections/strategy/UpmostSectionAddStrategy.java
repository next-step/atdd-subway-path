package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public class UpmostSectionAddStrategy implements SectionAddStrategy {
    @Override
    public boolean meetCondition(Sections sections, Section newSection) {
        List<Section> sectionsList = sections.getValue();
        if (sectionsList.isEmpty()) {
            return false;
        }

        Section firstSection = sectionsList.get(0);
        return newSection.isSameDownStation(firstSection.getUpStation().getId());
    }

    @Override
    public ChangeableSections findChangeableSections(Sections sections, Section newSection) {
        return new ChangeableSections(List.of(), List.of());
    }
}
