package nextstep.subway.controller.resonse;

import nextstep.subway.domain.vo.Path;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stationResponses;
    private long distance;

    public PathResponse(List<StationResponse> stationResponses, long distance) {
        this.stationResponses = stationResponses;
        this.distance = distance;
    }

    public List<StationResponse> getStationResponses() {
        return stationResponses;
    }

    public long getDistance() {
        return distance;
    }
}
