package nextstep.subway.path.domain;

import nextstep.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class ShortestPathResult {
    private final List<StationResponse> stations;
    private final Double weight;

    public ShortestPathResult() {
        this.stations = Collections.emptyList();
        this.weight = 0.0;
    }

    private ShortestPathResult(List<StationResponse> stations, Double weight) {
        this.stations = stations;
        this.weight = weight;
    }

    public static ShortestPathResult empty() {
        return new ShortestPathResult();
    }

    public static ShortestPathResult withResult(List<StationResponse> stations, Double weight) {
        return new ShortestPathResult(stations, weight);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Double getWeight() {
        return weight;
    }
}
