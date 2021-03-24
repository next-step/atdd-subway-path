package nextstep.subway.path;

import nextstep.subway.station.domain.Station;

import java.util.List;

public class PathResponse {

    private List<Station> stations;
    private int distance;

    public List<Station> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
