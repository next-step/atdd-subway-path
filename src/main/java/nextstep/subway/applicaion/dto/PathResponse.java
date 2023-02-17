package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;
    private long distance;

    public PathResponse(final List<StationResponse> stations, final double distance) {
        this.stations = stations;
        this.distance = Math.round(distance);
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
