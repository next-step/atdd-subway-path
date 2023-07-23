package nextstep.subway.line.domain.entity.handler.addition;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.vo.Sections;

public class AddSectionAtFirstHandler extends SectionAdditionHandler{
    public AddSectionAtFirstHandler(SectionAdditionHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.getFirstStation().equals(section.getDownStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionUpStationIsNewcomer(sections, section);
        if (nextHandler != null) {
            nextHandler.validate(sections, section);
        }
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        sections.forceSectionAddition(newSection);
    }

    private void validateNewSectionUpStationIsNewcomer(Sections sections, Section section) {
        if (sections.hasStation(section.getUpStation())) {
            throw new CreationValidationException("section.0001");
        }
    }
}
