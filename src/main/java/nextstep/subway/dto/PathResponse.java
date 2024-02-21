package nextstep.subway.dto;

import nextstep.subway.entity.Station;

import java.util.List;

public class PathResponse {
    private final List<Station> stations;
    private final Integer distance;

    public PathResponse(List<Station> stations, Integer distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
