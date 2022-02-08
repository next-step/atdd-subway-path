package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {

    private final List<StationResponse> stations = new ArrayList<>();
    private final int distance;

    public PathResponse(List<Station> stations, int distance) {
        for (Station station : stations) {
            this.stations.add(StationResponse.createStationResponse(station));
        }
        this.distance = distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return "PathResponse{" +
                "stations=" + stations +
                ", distance=" + distance +
                '}';
    }
}
