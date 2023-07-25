package nextstep.subway.domain.vo;

import nextstep.subway.domain.Station;

import java.util.List;


public class Path {

    private List<Station> stations;
    private long distance;

    public Path(List<Station> stations, long distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<Station> getStations() {
        return stations;
    }

    public long getDistance() {
        return distance;
    }
}
