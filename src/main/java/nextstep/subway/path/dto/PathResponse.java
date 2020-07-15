package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private Long distance;
    private Long duration;

    public PathResponse(List<StationResponse> stations, Long distance, Long duration) {
        this.stations = stations;
        this.distance = distance;
        this.duration = duration;
    }

    public static PathResponse of(List<StationResponse> stations, Long distance, Long duration) {
        return new PathResponse(stations, distance, duration);
    }

    public PathResponse() {
    }


    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }

    public Long getDuration() {
        return duration;
    }
}
