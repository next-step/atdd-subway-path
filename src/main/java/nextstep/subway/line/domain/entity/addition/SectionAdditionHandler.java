package nextstep.subway.line.domain.entity.addition;

import nextstep.subway.line.domain.entity.Section;
import nextstep.subway.line.domain.vo.Sections;

public abstract class SectionAdditionHandler {

    public abstract boolean checkApplicable(Sections sections, Section section);

    public abstract void validate(Sections sections, Section section);

    public abstract void apply(Sections sections, Section newSection);
}
