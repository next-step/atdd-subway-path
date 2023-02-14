package nextstep.subway.domain.sections.strategy;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public interface SectionDeleteStrategy {
    boolean meetCondition(Sections sections, Section downmostSection);

    ChangeableSections findChangeableSections(Sections sections, Section downmostSection, Line line);
}
