package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class PathResponse {
    private final List<StationResponse> stations;
    private final Double weight;
    private final Integer duration;
    private final Integer distance;

    protected PathResponse() {
        this.stations = Collections.emptyList();
        this.weight = 0.0;
        this.duration = 0;
        this.distance = 0;
    }

    private PathResponse(List<StationResponse> stations, Double weight, Integer duration, Integer distance) {
        this.stations = stations;
        this.weight = weight;
        this.duration = duration;
        this.distance = distance;
    }

    public static PathResponse with(List<StationResponse> stationResponses, Double weight, Integer duration, Integer distance) {
        return new PathResponse(stationResponses, weight, duration, distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getDistance() {
        return distance;
    }
}
