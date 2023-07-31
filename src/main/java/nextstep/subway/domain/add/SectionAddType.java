package nextstep.subway.domain.add;

import nextstep.subway.domain.Section;
import nextstep.subway.domain.Sections;

import java.util.Arrays;

public enum SectionAddType {
    LAST(new AddLast()),
    FIRST(new AddFirst()),
    MIDDLE_UP_STATION(new AddMiddleByUpStation()),
    MIDDLE_DOWN_STATION(new AddMiddleByDownStation());

    private final SectionAddStrategy strategy;

    SectionAddType(SectionAddStrategy strategy) {
        this.strategy = strategy;
    }

    public static SectionAddStrategy find(Sections sections, Section section) {
        return Arrays.stream(SectionAddType.values())
                .filter(type -> type.strategy.match(sections, section))
                .findFirst()
                .orElseThrow()
                .strategy;
    }
}
