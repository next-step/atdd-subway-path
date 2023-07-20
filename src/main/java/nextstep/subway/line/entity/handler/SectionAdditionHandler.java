package nextstep.subway.line.entity.handler;

import nextstep.subway.line.entity.Section;
import nextstep.subway.line.entity.Sections;

public interface SectionAdditionHandler {

    boolean checkApplicable(Sections sections, Section section);
}
