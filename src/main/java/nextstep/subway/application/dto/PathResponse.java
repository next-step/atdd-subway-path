package nextstep.subway.application.dto;

import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private int distance;

    public PathResponse(final List<Station> stations, final int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    private StationResponse createStationResponse(Station station) {
        return new StationResponse(station.getId(), station.getName());
    }
}
