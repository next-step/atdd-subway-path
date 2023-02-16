package nextstep.subway.domain.sections.strategy;

import java.util.List;
import java.util.function.Predicate;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public class MiddleSectionDeleteStrategy implements SectionDeleteStrategy {
    @Override
    public boolean isValidCondition(Sections sections, Long stationId) {
        List<Section> sectionsList = sections.getValue();
        if (sectionsList.isEmpty()) {
            return false;
        }

        Section filteredSection = findSection(sections, section -> section.isSameDownStation(stationId));

        return sectionsList.indexOf(filteredSection) != sectionsList.size() - 1;
    }

    @Override
    public ChangeableSections findChangeableSections(Sections sections, Long stationId, Line line) {
        Section deprecatedTargetSection = findSection(sections, section -> section.isSameDownStation(stationId));
        Section deprecatedNextSection = findSection(sections, section -> section.isSameUpStation(stationId));

        Section newSection = new Section(line, deprecatedTargetSection.getUpStation(), deprecatedNextSection.getDownStation(), deprecatedTargetSection.getDistance() + deprecatedNextSection.getDistance());

        return new ChangeableSections(List.of(newSection), List.of(deprecatedTargetSection, deprecatedNextSection));
    }

    private Section findSection(Sections sections, Predicate<Section> predicate) {
        return sections.getValue().stream()
            .filter(predicate)
            .findFirst()
            .orElseThrow();
    }
}
