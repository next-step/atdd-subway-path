package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public class DownmostSectionAddStrategy implements SectionAddStrategy {
    @Override
    public boolean meetCondition(Sections sections, Section newSection) {
        List<Section> sectionsList = sections.getValue();
        if (sectionsList.isEmpty()) {
            return false;
        }

        Section lastSection = sectionsList.get(sectionsList.size() - 1);
        return lastSection.isSameDownStation(newSection.getUpStation().getId());
    }

    @Override
    public ChangeableSections findChangeableSections(Sections sections, Section newSection, Line line) {
        return new ChangeableSections(List.of(), List.of());
    }
}
