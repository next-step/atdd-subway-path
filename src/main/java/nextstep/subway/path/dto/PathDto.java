package nextstep.subway.path.dto;

import nextstep.subway.station.dto.StationDto;

import java.util.Set;

public class PathDto {

    private final Set<StationDto> stationDtos;
    private final int distance;

    public PathDto(Set<StationDto> stationDtos, int distance) {
        this.stationDtos = stationDtos;
        this.distance = distance;
    }

    public static PathDto from(Set<StationDto> stationDtos, int distance) {
        return new PathDto(stationDtos, distance);
    }

    public Set<StationDto> getStationDtos() {
        return stationDtos;
    }

    public int getDistance() {
        return distance;
    }
}
