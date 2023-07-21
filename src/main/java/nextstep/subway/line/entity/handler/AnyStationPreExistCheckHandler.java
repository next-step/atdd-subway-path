package nextstep.subway.line.entity.handler;

import nextstep.subway.common.exception.CreationValidationException;
import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AnyStationPreExistCheckHandler extends SectionAdditionHandler {
    public AnyStationPreExistCheckHandler(SectionAdditionHandler nextHandler) {
        super(nextHandler);
    }

    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return true;
    }


    @Override
    public void validate(Sections sections, Section section) {
        validateOnlyOneStationIsEnrolledInLine(sections, section);
        if (nextHandler != null) {
            nextHandler.validate(sections, section);
        }
    }

    @Override
    public void apply(Sections sections, Section newSection) {
        return;
    }

    private void validateOnlyOneStationIsEnrolledInLine(Sections sections, Section section) {
        if (sections.checkUpStationsContains(section.getUpStation()) &&
                sections.checkDownStationsContains(section.getDownStation())) {
            throw new CreationValidationException("section.0003");
        }
    }
}
