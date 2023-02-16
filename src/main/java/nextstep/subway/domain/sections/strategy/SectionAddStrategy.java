package nextstep.subway.domain.sections.strategy;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public interface SectionAddStrategy {
    boolean isValidCondition(Sections sections, Section newSection);

    ChangeableSections findChangeableSections(Sections sections, Section newSection, Line line);
}
