package nextstep.subway.domain;

import org.springframework.util.Assert;

import java.util.List;

public class SubwayPath {
    private List<Station> stations;
    private int totalDistance;

    public SubwayPath(List<Station> stations, int totalDistance) {
        Assert.notNull(stations, "stations must not be null");
        this.stations = stations;
        this.totalDistance = totalDistance;
    }

    public List<Station> getStations() {
        return this.stations;
    }

    public int totalDistance() {
        return this.totalDistance;
    }
}
