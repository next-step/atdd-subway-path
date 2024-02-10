package nextstep.subway.path.service.dto;

import nextstep.subway.station.service.dto.StationResponse;

import java.util.List;

public class PathResponse {
    private int distance;
    private List<StationResponse> stations;

    public int getDistance() {
        return distance;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
