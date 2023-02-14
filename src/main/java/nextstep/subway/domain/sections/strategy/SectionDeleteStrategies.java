package nextstep.subway.domain.sections.strategy;

import java.util.List;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.sections.Sections;

public class SectionDeleteStrategies {
    private final List<SectionDeleteStrategy> strategies;

    public SectionDeleteStrategies() {
        this.strategies = List.of();
    }

    public ChangeableSections findChangeableSections(Sections sections, Section downmostSection, Line line) {
        return strategies.stream()
            .filter(strategy -> strategy.meetCondition(sections, downmostSection))
            .map(strategy -> strategy.findChangeableSections(sections, downmostSection, line))
            .findFirst()
            .orElseGet(() -> new ChangeableSections(List.of(), List.of()));
        //.orElseThrow(() -> new CannotAddSectionException("던져지면 안되는 예외입니다."));
    }
}
