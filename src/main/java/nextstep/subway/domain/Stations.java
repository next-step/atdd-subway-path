package nextstep.subway.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Stations {
    private List<Station> stations = new ArrayList<>();

    private Stations() {
    }

    private Stations(List<Station> stations) {
        this.stations = stations;
    }

    public static Stations of() {
        return new Stations(Collections.emptyList());
    }

    public static Stations of(Station... stations) {
        return new Stations(new ArrayList<>(Arrays.asList(stations)));
    }

    public boolean isFinalDownStationEqualTo(Station station) {
        return getFinalDownStation().equals(station);
    }

    public Station getFinalDownStation() {
        return stations.get(stations.size() - 1);
    }

    public void add(Station station) {
        this.stations.add(station);
    }

    public List<Station> get() {
        return this.stations;
    }
}
