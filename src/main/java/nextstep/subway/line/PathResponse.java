package nextstep.subway.line;

import java.util.List;
import nextstep.subway.station.Station;

public class PathResponse {

    List<Station> stations;
    long distance;

    public PathResponse(List<Station> stations, long distance) {
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
