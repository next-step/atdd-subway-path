package nextstep.subway.path.dto;

import java.util.List;

public class PathFinderResult {

    private final List<Long> stationIds;
    private final double weight;

    public PathFinderResult(List<Long> stationIds, double weight) {
        this.stationIds = stationIds;
        this.weight = weight;
    }

    public List<Long> getStationIds() {
        return stationIds;
    }

    public double getWeight() {
        return weight;
    }
}
