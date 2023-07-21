package nextstep.subway.line.entity.handler;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AddSectionAtMiddleLeftHandler extends SectionAdditionHandler {
    public AddSectionAtMiddleLeftHandler(SectionAdditionHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.checkUpStationsContains(section.getUpStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionLengthSmaller(sections.getSectionByUpStation(section.getUpStation()), section);
        if (nextHandler != null) {
            nextHandler.validate(sections, section);
        }
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
