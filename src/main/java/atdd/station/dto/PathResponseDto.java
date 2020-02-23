package atdd.station.dto;

import java.util.List;
import java.util.Objects;

public class PathResponseDto {

    private Long startStationId;
    private Long endStationId;
    private List<PathStation> stations;

    private PathResponseDto() { }

    public static PathResponseDto of(Long startStationId,
                                     Long endStationId,
                                     List<PathStation> stations) {

        PathResponseDto responseDto = new PathResponseDto();
        responseDto.startStationId = startStationId;
        responseDto.endStationId = endStationId;
        responseDto.stations = stations;
        return responseDto;
    }

    public Long getStartStationId() {
        return startStationId;
    }

    public Long getEndStationId() {
        return endStationId;
    }

    public List<PathStation> getStations() {
        return stations;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PathResponseDto)) return false;
        PathResponseDto that = (PathResponseDto) o;
        return Objects.equals(startStationId, that.startStationId) &&
                Objects.equals(endStationId, that.endStationId) &&
                Objects.equals(stations, that.stations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startStationId, endStationId, stations);
    }

    @Override
    public String toString() {
        return "PathResponseDto{" +
                "startStationId=" + startStationId +
                ", endStationId=" + endStationId +
                ", stations=" + stations +
                '}';
    }

}
