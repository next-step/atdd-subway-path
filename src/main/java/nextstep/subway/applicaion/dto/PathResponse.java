package nextstep.subway.applicaion.dto;

import nextstep.subway.domain.Path;
import nextstep.subway.domain.Station;

import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private Integer distance;

    public PathResponse(List<Station> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public static PathResponse from(Path path) {
        return new PathResponse(path.getPathStations(), path.getDistance());
    }

    public List<Station> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
