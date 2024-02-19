package nextstep.subway.service.dto;

import java.util.List;

public class PathDto {
    private List<StationDto> stations;
    private int distance;

    public PathDto(List<StationDto> stations, int distance) {
        this.stations = stations;
        this.distance = distance;
    }

    public List<StationDto> getStations() {
        return stations;
    }

    public int getDistance() {
        return distance;
    }
}
