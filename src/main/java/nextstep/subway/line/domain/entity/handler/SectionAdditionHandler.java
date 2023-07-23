package nextstep.subway.line.domain.entity.handler;

import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.vo.Sections;

public abstract class SectionAdditionHandler {

    SectionAdditionHandler nextHandler;

    public SectionAdditionHandler(SectionAdditionHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    public abstract boolean checkApplicable(Sections sections, Section section);

    public void validate(Sections sections, Section section) {
        if (nextHandler != null) {
            nextHandler.validate(sections, section);
        }
    }

    public abstract void apply(Sections sections, Section newSection);
}
