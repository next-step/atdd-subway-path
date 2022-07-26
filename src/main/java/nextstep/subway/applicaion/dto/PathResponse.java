package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;

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

    public static PathResponse of(Path path, Station target, Station source) {
        List<StationResponse> stations = path.findShortestPath(target, source).stream()
                .map(StationResponse::of)
                .collect(Collectors.toList());
        int distance = path.getShortestDistance();

        return new PathResponse(stations, distance);
    }


    public List<StationResponse> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}