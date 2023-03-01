package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Path;

import java.util.List;
import java.util.stream.Collectors;

public class PathResponse {

    private List<StationResponse> stations;

    private Integer distance;

    public PathResponse() {
    }

    public PathResponse(List<StationResponse> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public PathResponse(Path path) {
        this.stations = path.getStations().stream()
                .map(station -> new StationResponse(station.getId(), station.getName()))
                .collect(Collectors.toList());
        this.distance = path.getDistance();
    }

    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
