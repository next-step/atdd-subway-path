package nextstep.subway.path.dto;

import nextstep.subway.station.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<PathStationResponse> stations;
    private int distance;
    private int duration;

    public PathResponse() {
    }

    public PathResponse(List<PathStationResponse> stations, int distance, int duration) {
        this.stations = stations;
        this.distance = distance;
        this.duration = duration;
    }

    public static PathResponse of(List<Station> stations, int distance, int duration) {
        List<PathStationResponse> pathStationResponses = stations.stream().map(PathStationResponse::of).collect(Collectors.toList());
        return new PathResponse(pathStationResponses, distance, duration);
    }

    public List<PathStationResponse> getStations() {
        return stations;
    }

    public void setStations(final List<PathStationResponse> stations) {
        this.stations = stations;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(final int distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(final int duration) {
        this.duration = duration;
    }
}
