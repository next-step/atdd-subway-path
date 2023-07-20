package nextstep.subway.line.entity.handler;

import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

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
