package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.Collections;
import java.util.List;

public class PathResponse {
    private final List<StationResponse> stations;
    private final Integer distance;
    private final Integer duration;

    protected PathResponse() {
        this.stations = Collections.emptyList();
        this.duration = 0;
        this.distance = 0;
    }

    private PathResponse(List<StationResponse> stations, Integer distance, Integer duration) {
        this.stations = stations;
        this.duration = duration;
        this.distance = distance;
    }

    public static PathResponse with(List<StationResponse> stationResponses, Integer distance, Integer duration) {
        return new PathResponse(stationResponses, distance, duration);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDuration() {
        return duration;
    }

    public Integer getDistance() {
        return distance;
    }
}
