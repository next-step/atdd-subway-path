package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.exception.CannotAddSectionException;
import nextstep.subway.domain.sections.Sections;

public class SectionAddStrategies {
    private final List<SectionAddStrategy> strategies;

    public SectionAddStrategies() {
        this.strategies = List.of(
            new UpmostSectionAddStrategy(),
            new MiddleSectionAddStrategy(),
            new DownmostSectionAddStrategy()
        );
    }

    public ChangeableSections findChangeableSections(Sections sections, Section newSection, Line line) {
        return strategies.stream()
            .filter(strategy -> strategy.isValidCondition(sections, newSection))
            .map(strategy -> strategy.findChangeableSections(sections, newSection, line))
            .findFirst()
            .orElseThrow(() -> new CannotAddSectionException("던져지면 안되는 예외입니다."));
    }
}
