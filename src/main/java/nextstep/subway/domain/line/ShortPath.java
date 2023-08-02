package nextstep.subway.domain.line;

import nextstep.subway.domain.station.Station;

import java.util.List;

public class ShortPath {

    List<Station> stations;
    int distance;

    public ShortPath(List<Station> stations, Double distance) {
        this.stations = stations;
        this.distance = distance.intValue();
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
