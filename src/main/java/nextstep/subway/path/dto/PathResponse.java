package nextstep.subway.path.dto;

import java.util.List;

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
