package nextstep.subway.station.domain;

import java.util.List;

public class Stations {

    private List<Station> stations;
    private double distance;

    public Stations(List<Station> stations) {
        this.stations = stations;
    }

    public Stations(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}

