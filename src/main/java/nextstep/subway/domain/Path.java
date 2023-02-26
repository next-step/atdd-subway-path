package nextstep.subway.domain;

import java.util.List;

public class Path {

    private final List<Long> stationIds;
    private final long distance;

    public Path(List<Long> stationIds, long distance) {
        this.stationIds = stationIds;
        this.distance = distance;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public long getDistance() {
        return distance;
    }
}
