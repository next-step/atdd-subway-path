package nextstep.subway.path;

import nextstep.subway.station.Station;

import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private double distance;

    public PathResponse(List<Station> stations, double distance) {
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
