package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;
    private int distance;

    public List<StationResponse> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
