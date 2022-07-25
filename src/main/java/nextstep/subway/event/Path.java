package nextstep.subway.event;

import nextstep.subway.domain.Station;

import java.util.List;

public class Path {
    private List<Station> stations;
    private double distance;

    public Path(List<Station> stations, double distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return (int) distance;
    }
}
