package nextstep.subway.domain.sections.strategy;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.sections.Sections;

public interface SectionDeleteStrategy {
    boolean isValidCondition(Sections sections, Long stationId);

    ChangeableSections findChangeableSections(Sections sections, Long stationId, Line line);
}
