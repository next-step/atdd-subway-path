package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class PathResponse {
    private final List<StationResponse> stations;
    private final Double weight;

    protected PathResponse() {
        this.stations = Collections.emptyList();
        this.weight = 0.0;
    }

    private PathResponse(List<StationResponse> stations, Double weight) {
        this.stations = stations;
        this.weight = weight;
    }

    public static PathResponse with(List<StationResponse> stationResponses, Double distance) {
        return new PathResponse(stationResponses, distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Double getWeight() {
        return weight;
    }
}
