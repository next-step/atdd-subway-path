package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationResponse;

import java.util.List;

public class PathResponse {

    private List<StationResponse> stations;
    private Integer distance;
    private Integer duration;

    public List<StationResponse> getStations() {
        return stations;
    }
}
