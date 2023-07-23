package nextstep.subway.line.domain.entity.handler.addition;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.vo.Sections;

public class AddSectionAtLastHandler extends SectionAdditionHandler {
    public AddSectionAtLastHandler(SectionAdditionHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.equalsLastStation(section.getUpStation());
    }

    @Override
    public void validate(Sections sections, Section section) {
        validateNewSectionDownStationIsNewcomer(sections, section);
        if (nextHandler != null) {
            nextHandler.validate(sections, section);
        }
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        sections.forceSectionAddition(newSection);
    }

    private void validateNewSectionDownStationIsNewcomer(Sections sections, Section section) {
        if (sections.hasStation(section.getDownStation())) {
            throw new CreationValidationException("section.0001");
        }
    }
}
