package nextstep.subway.domain;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public enum SectionDeleteType {
    FIRST(Stations::equalFirstStation, Sections::removeFirst),
    LAST(Stations::equalLastStation, Sections::removeLast),
    MIDDLE((stations, station) -> false, Sections::removeMiddle);

    private final BiPredicate<Stations, Station> condition;
    private final BiConsumer<Sections, Station> remove;

    SectionDeleteType(BiPredicate<Stations, Station> condition, BiConsumer<Sections, Station> remove) {
        this.condition = condition;
        this.remove = remove;
    }

    public static SectionDeleteType find(Stations stations, Station station) {
        return Arrays.stream(SectionDeleteType.values())
                .filter(type -> type.condition.test(stations, station))
                .findFirst()
                .orElse(MIDDLE);
    }

    public void apply(Sections sections, Station station) {
        remove.accept(sections, station);
    }
}
