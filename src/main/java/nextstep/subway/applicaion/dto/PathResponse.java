package nextstep.subway.applicaion.dto;

import java.util.ArrayList;
import java.util.List;

public class PathResponse {

    private final List<StationResponse> stationList;

    public List<StationResponse> getStationList() {
        return stationList;
    }

    public PathResponse(List<StationResponse> stationList) {
        this.stationList = stationList;
    }
}
