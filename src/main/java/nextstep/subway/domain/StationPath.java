package nextstep.subway.domain;

import java.util.List;

public class StationPath {

    private final List<Station> stationList;
    private final long distance;

    public StationPath(final List<Station> stationList, final long distance) {
        this.stationList = stationList;
        this.distance = distance;
    }

    public int numOfStations() {
        return stationList.size();
    }

    public long distance() {
        return distance;
    }
}
