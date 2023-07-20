package nextstep.subway.line.entity.handler;

import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AddSectionAtFirstHandler implements SectionAdditionHandler{
    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return true;
    }

    @Override
    public void apply(Sections sections, Section newSection) {

    }
}
