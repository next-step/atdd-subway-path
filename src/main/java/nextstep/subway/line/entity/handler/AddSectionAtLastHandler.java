package nextstep.subway.line.entity.handler;

import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public class AddSectionAtLastHandler implements SectionAdditionHandler{
    @Override
    public boolean checkApplicable(Sections sections, Section section) {
        return sections.equalsLastStation(section.getUpStation());
    }
}
