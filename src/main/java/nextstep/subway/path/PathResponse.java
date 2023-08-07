package nextstep.subway.path;

import nextstep.subway.station.StationResponse;

import java.util.List;

public class PathResponse {
    private final List<StationResponse> stations;
    private final long distance;

    public PathResponse(final List<StationResponse> stations, final long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
