package nextstep.subway.domain.delete;

import nextstep.subway.domain.Station;
import nextstep.subway.domain.Stations;

import java.util.Arrays;

public enum SectionDeleteType {
    FIRST(new DeleteFirst()),
    LAST(new DeleteLast()),
    MIDDLE(new DeleteMiddle());

    private final SectionDeleteStrategy strategy;

    SectionDeleteType(SectionDeleteStrategy strategy) {
        this.strategy = strategy;
    }

    public static SectionDeleteStrategy find(Stations stations, Station station) {
        return Arrays.stream(SectionDeleteType.values())
                .filter(type -> type.strategy.match(stations, station))
                .findFirst()
                .orElse(MIDDLE)
                .strategy;
    }
}
