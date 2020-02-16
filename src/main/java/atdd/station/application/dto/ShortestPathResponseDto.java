package atdd.station.application.dto;

import java.util.List;

public class ShortestPathResponseDto {
    private List<StationResponseDto> stations;

    public ShortestPathResponseDto() {
    }

    public ShortestPathResponseDto(List<StationResponseDto> stations) {
        this.stations = stations;
    }

    public static ShortestPathResponseDto of(List<StationResponseDto> stations) {
        return new ShortestPathResponseDto(stations);
    }

    public List<StationResponseDto> getStations() {
        return stations;
    }
}
