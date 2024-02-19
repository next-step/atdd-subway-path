package nextstep.subway.application.dto;

import nextstep.subway.entity.Station;

import java.util.List;

public class PathResult {
    private final List<Station> stations;
    private final Integer distance;

    public PathResult(List<Station> stations, Integer distance) {
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
