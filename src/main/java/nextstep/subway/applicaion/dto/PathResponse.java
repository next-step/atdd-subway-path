package nextstep.subway.applicaion.dto;

import java.util.List;

public class PathResponse {
    private List<StationResponse> stations;

    public PathResponse(List<StationResponse> stations) {
        this.stations = stations;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}

