package nextstep.subway.path.payload;

import nextstep.subway.station.payload.StationResponse;

import java.util.List;

public class ShortestPathResponse {
    private List<StationResponse> stations;
    private Long distance;

    public ShortestPathResponse(final List<StationResponse> stations, final Long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Long getDistance() {
        return distance;
    }
}
