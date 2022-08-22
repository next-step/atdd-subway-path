package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private Double distance;
    private List<StationResponse> stations;

    public PathResponse(List<Station> stations, Double distance) {
        createStationResponses(stations);
        this.distance = distance;
    }

    public void createStationResponses(List<Station> stations) {
        this.stations = stations.stream().map((station) -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Double getDistance() {
        return distance;
    }
}
