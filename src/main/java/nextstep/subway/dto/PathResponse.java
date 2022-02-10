package nextstep.subway.dto;

import nextstep.subway.applicaion.dto.StationResponse;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations;
    private int distance;

    public PathResponse(List<StationResponse> stations,int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse of(List<Station> stations,int distance) {
        return new PathResponse(convertStationResponse(stations), distance);
    }
    private List<StationResponse> convertStationResponse(List<Station> stations) {
        return stations.stream().map(station -> new StationResponse(station)).collect(Collectors.toList());
    }

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "PathResponse{" +
                "stations=" + stations +
                ", distance=" + distance +
                '}';
    }
}
