package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private Integer distance;
    private Integer duration;

    public PathResponse(List<StationResponse> stations, Integer distance, Integer duration) {
        this.stations = stations;
        this.distance = distance;
        this.duration = duration;
    }

    public PathResponse() {
    }

    public static PathResponse of(List<StationResponse> stations, Integer distance, Integer duration) {
        return new PathResponse(stations, distance, duration);
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
