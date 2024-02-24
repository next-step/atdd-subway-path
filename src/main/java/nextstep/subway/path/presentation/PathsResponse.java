package nextstep.subway.path.presentation;

import nextstep.subway.path.domain.dto.StationDto;

import java.util.List;

public class PathsResponse {

    private int distance;
    private List<StationDto> stationDtoList;

    public PathsResponse(int distance, List<StationDto> stationDtoList) {
        this.distance = distance;
        this.stationDtoList = stationDtoList;
    }

    public int getDistance() {
        return distance;
    }

    public List<StationDto> getStationDtoList() {
        return stationDtoList;
    }
}
