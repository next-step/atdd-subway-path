package nextstep.subway.dto.path;

import nextstep.subway.dto.station.StationResponse;
import nextstep.subway.entity.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {
    private List<StationResponse> stations = new ArrayList<>();
    private Integer distance;

    protected PathResponse() {}

    public PathResponse(List<Station> stations, Integer distance) {
        this.stations.addAll(
            createStationResponses(stations)
        );
        this.distance = distance;
    }

    private List<StationResponse> createStationResponses(List<Station> stations) {
        return stations.stream()
            .map(station -> new StationResponse(station.getId(), station.getName()))
            .collect(Collectors.toList());
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public double getDistance() {
        return distance;
    }
}
