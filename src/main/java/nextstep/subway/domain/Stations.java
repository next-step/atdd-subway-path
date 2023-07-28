package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import nextstep.subway.exception.ErrorType;
import nextstep.subway.exception.SectionAddException;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stations {
    private List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public SectionAddType findAddType(Station upStation, Station downStation) {
        if (!stations.isEmpty() && doesNotContainAll(upStation, downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_NOT_EXIST_IN_LINE);
        }
        if (containsAll(upStation, downStation)) {
            throw new SectionAddException(ErrorType.STATIONS_EXIST_IN_LINE);
        }

        if (stations.isEmpty() || equalLastStation(upStation)) {
            return SectionAddType.LAST;
        }
        if (equalFirstStation(downStation)) {
            return SectionAddType.FIRST;
        }
        return SectionAddType.MIDDLE;
    }

    private boolean doesNotContainAll(Station upStation, Station downStation) {
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    private boolean containsAll(Station upStation, Station downStation) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    private boolean equalLastStation(Station upStation) {
        return stations.get(stations.size() - 1).equals(upStation);
    }

    private boolean equalFirstStation(Station downStation) {
        return stations.get(0).equals(downStation);
    }
}
