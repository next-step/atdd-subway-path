package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class ExploredResult {
    private List<Station> stations;
    private int distance;

    private ExploredResult(List<Station> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static ExploredResult of(List<Station> stations, double distance) {
        return new ExploredResult(stations, (int)distance);
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
