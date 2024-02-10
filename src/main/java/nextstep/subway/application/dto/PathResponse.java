package nextstep.subway.application.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse(final List<StationResponse> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
