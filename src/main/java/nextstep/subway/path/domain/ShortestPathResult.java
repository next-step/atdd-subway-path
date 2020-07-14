package nextstep.subway.path.domain;

import nextstep.subway.station.domain.Station;

import java.util.Collections;
import java.util.List;

public class ShortestPathResult {
    private final List<Station> stations;
    private final Integer distance;
    private final Integer duration;

    public ShortestPathResult() {
        this.stations = Collections.emptyList();
        this.distance = 0;
        this.duration = 0;
    }

    private ShortestPathResult(List<Station> stations, Integer distance, Integer duration) {
        this.stations = stations;
        this.distance = distance;
        this.duration = duration;
    }

    public static ShortestPathResult empty() {
        return new ShortestPathResult();
    }

    public List<Station> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
