package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.exception.CannotDeleteSectionException;
import nextstep.subway.domain.sections.Sections;

public class SectionDeleteStrategies {
    private final List<SectionDeleteStrategy> strategies;

    public SectionDeleteStrategies() {
        this.strategies = List.of(
            new DownmostSectionDeleteStrategy(),
            new MiddleSectionDeleteStrategy()
        );
    }

    public ChangeableSections findChangeableSections(Sections sections, Long stationId, Line line) {
        return strategies.stream()
            .filter(strategy -> strategy.isValidCondition(sections, stationId))
            .map(strategy -> strategy.findChangeableSections(sections, stationId, line))
            .findFirst()
            .orElseThrow(() -> new CannotDeleteSectionException("던져지면 안되는 예외입니다."));
    }
}
