package nextstep.subway.dto;

import nextstep.subway.entity.Station;

import java.util.List;

public class PathResponse {
    private List<Station> stations;
    private Integer distance;

    public List<Station> getStations() {
        return stations;
    }

    public Integer getDistance() {
        return distance;
    }
}
