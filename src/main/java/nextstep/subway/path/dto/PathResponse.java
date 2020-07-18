package nextstep.subway.path.dto;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import nextstep.subway.station.dto.StationResponse;

public class PathResponse {
    private final List<StationResponse> stationResponses;
    private final Double weight;

    public PathResponse(List<StationResponse> stationResponses, Double weight) {
        this.stationResponses = stationResponses;
        this.weight = weight;
    }

    private PathResponse() {
        this.stationResponses = Collections.emptyList();
        this.weight = 0.0;
    }

    public static PathResponse empty() {
        return new PathResponse();
    }

    public static PathResponse of(ShortestPathResult pathResponse) {
        return new PathResponse(pathResponse.getStations(), pathResponse.getWeight());
    }

    public List<StationResponse> getStationResponses() {
        return Optional.ofNullable(stationResponses).orElseThrow(() -> new RuntimeException("not found response"));
    }

    public Double getWeight() {
        return Optional.ofNullable(weight).orElse(0.0);
    }
}
