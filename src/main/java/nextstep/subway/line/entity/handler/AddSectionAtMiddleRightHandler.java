package nextstep.subway.line.entity.handler;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AddSectionAtMiddleRightHandler extends SectionAdditionHandler {
    public AddSectionAtMiddleRightHandler(SectionAdditionHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.checkDownStationsContains(section.getDownStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionLengthSmaller(sections.getSectionByDownStation(section.getDownStation()), section);
        if (nextHandler != null) {
            nextHandler.validate(sections, section);
        }
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        Section sectionByDownStation = sections.getSectionByDownStation(newSection.getDownStation());
        sectionByDownStation.changeDownStation(newSection.getUpStation());
        sections.forceSectionAddition(newSection);
    }

    private static void validateNewSectionLengthSmaller(Section originalSection, Section section) {
        if (section.getDistance().compareTo(originalSection.getDistance()) != -1) {
            throw new CreationValidationException(String.format("구간의 길이가 기존 구간 보다 작아야합니다. 기존 구간 길이:%s 새 구간 길이:%s", originalSection.getDistance(), section.getDistance()));
        }
    }
}
