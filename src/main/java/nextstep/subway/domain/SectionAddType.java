package nextstep.subway.domain;

import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import java.util.function.BiConsumer;

public enum SectionAddType {
    FIRST(Sections::addFirst),
    LAST(Sections::addLast),
    MIDDLE_UP_STATION(Sections::addMiddleUpStation),
    MIDDLE_DOWN_STATION(Sections::addMiddleDownStation);

    private final BiConsumer<Sections, Section> add;

    SectionAddType(BiConsumer<Sections, Section> add) {
        this.add = add;
    }

    public static SectionAddType find(Sections sections, Station upStation, Station downStation) {
        Stations stations = new Stations(sections.getStations());
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
        if (sections.containsAtUpStation(upStation)) {
            return SectionAddType.MIDDLE_UP_STATION;
        }
        return SectionAddType.MIDDLE_DOWN_STATION;
    }

    public void apply(Sections sections, Section section) {
        add.accept(sections, section);
    }
}
