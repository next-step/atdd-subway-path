package nextstep.subway.domain;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stations {
    private List<Station> stations;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public boolean doesNotContainAll(Station upStation, Station downStation) {
        return !stations.contains(upStation) && !stations.contains(downStation);
    }

    public boolean containsAll(Station upStation, Station downStation) {
        return stations.contains(upStation) && stations.contains(downStation);
    }

    public boolean equalLastStation(Station upStation) {
        return stations.get(stations.size() - 1).equals(upStation);
    }

    public boolean equalFirstStation(Station downStation) {
        return stations.get(0).equals(downStation);
    }

    public boolean empty() {
        return stations.isEmpty();
    }
}
