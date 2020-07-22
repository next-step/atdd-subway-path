package nextstep.subway.path.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nextstep.subway.station.dto.StationResponse;

public class PathResponse {
    private final List<StationResponse> stationResponses;
    private final Double weight;
    private final Integer duration;
    private final Integer distance;

    public PathResponse(List<StationResponse> stationResponses, Double weight, Integer duration, Integer distance) {
        this.stationResponses = stationResponses;
        this.weight = weight;
        this.duration = duration;
        this.distance = distance;
    }

    private PathResponse() {
        this.stationResponses = Collections.emptyList();
        this.weight = 0.0;
        this.duration = 0;
        this.distance = 0;
    }

    public static PathResponse empty() {
        return new PathResponse();
    }

    public static PathResponse of(List<StationResponse> stations, Double weight, int duration, int distance) {
        return new PathResponse(stations, weight, duration, distance);
    }

    public List<StationResponse> getStationResponses() {
        return Optional.ofNullable(stationResponses).orElseThrow(() -> new RuntimeException("not found response"));
    }

    public Double getWeight() {
        return Optional.ofNullable(weight).orElse(0.0);
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getDistance() {
        return distance;
    }
}
