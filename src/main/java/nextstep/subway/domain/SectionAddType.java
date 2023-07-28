package nextstep.subway.domain;

import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import java.util.function.BiConsumer;

public enum SectionAddType {
    FIRST(Sections::addFirst),
    MIDDLE(Sections::addMiddle),
    LAST(Sections::addLast);

    private final BiConsumer<Sections, Section> add;

    SectionAddType(BiConsumer<Sections, Section> add) {
        this.add = add;
    }

    public static SectionAddType find(Stations stations, Station upStation, Station downStation) {
        if (!stations.empty() && stations.doesNotContainAll(upStation, downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_NOT_EXIST_IN_LINE);
        }
        if (stations.containsAll(upStation, downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_EXIST_IN_LINE);
        }

        if (stations.empty() || stations.equalLastStation(upStation)) {
            return SectionAddType.LAST;
        }
        if (stations.equalFirstStation(downStation)) {
            return SectionAddType.FIRST;
        }
        return SectionAddType.MIDDLE;
    }

    public void apply(Sections sections, Section section) {
        add.accept(sections, section);
    }
}
