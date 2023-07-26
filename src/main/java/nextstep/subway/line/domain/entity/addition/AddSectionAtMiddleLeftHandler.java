package nextstep.subway.line.domain.entity.addition;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.vo.Sections;

public class AddSectionAtMiddleLeftHandler extends SectionAdditionHandler {

    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.checkUpStationsContains(section.getUpStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionLengthSmaller(sections.getSectionByUpStation(section.getUpStation()), section);
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        Section sectionByUpStation = sections.getSectionByUpStation(newSection.getUpStation());
        sectionByUpStation.changeUpStation(newSection.getDownStation());
        sections.forceSectionAddition(newSection);
    }

    private void validateNewSectionLengthSmaller(Section originalSection, Section section) {
        if (section.getDistance().compareTo(originalSection.getDistance()) != -1) {
            throw new CreationValidationException("section.0002");
        }
    }
}
