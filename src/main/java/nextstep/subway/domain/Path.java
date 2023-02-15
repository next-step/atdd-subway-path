package nextstep.subway.domain;

import java.util.List;

public class Path {
    private List<Long> stationIds;
    private int distance;

    public Path(List<Long> stationIds, int distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public int getDistance() {
        return distance;
    }
}
