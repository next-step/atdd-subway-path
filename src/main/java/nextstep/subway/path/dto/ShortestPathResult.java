package nextstep.subway.path.dto;

import java.util.Collections;
import java.util.List;

import nextstep.subway.station.dto.StationResponse;

public class ShortestPathResult {

    private final List<StationResponse> stations;
    private final Integer distance;
    private final Integer duration;

    public ShortestPathResult() {
        this.stations = Collections.emptyList();
        this.distance = 0;
        this.duration = 0;
    }

    private ShortestPathResult(List<StationResponse> stationResponse, Integer distance, Integer duration) {
        this.stations = stationResponse;
        this.distance = distance;
        this.duration = duration;
    }

    public static ShortestPathResult empty() {
        return new ShortestPathResult();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }

    public Integer getDuration() {
        return duration;
    }
}
