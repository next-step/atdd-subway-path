package nextstep.subway.map.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stationResponses;
    private int distance;
    private int duration;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stationResponses, int distance, int duration) {
        this.stationResponses = stationResponses;
        this.distance = distance;
        this.duration = duration;
    }

    public static PathResponse of(List<StationResponse> stationResponses, int distances, int durations) {
        return new PathResponse(stationResponses, distances, durations);
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public int getDistance() {
        return distance;
    }

    public int getDuration() {
        return duration;
    }
}
