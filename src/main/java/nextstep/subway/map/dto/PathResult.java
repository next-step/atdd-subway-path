package nextstep.subway.map.dto;

import java.util.List;

public class PathResult {
    private List<Long> stationIds;
    private double weight;

    public PathResult(List<Long> stationIds, double weight) {
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
