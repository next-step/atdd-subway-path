package nextstep.subway.path.dto;

import nextstep.subway.section.entity.Sections;
import nextstep.subway.station.dto.StationDto;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PathDto {

    private final Set<StationDto> stationDtos;
    private final int distance;

    public PathDto(Set<StationDto> stationDtos, int distance) {
        this.stationDtos = stationDtos;
        this.distance = distance;
    }

    public static PathDto from(Sections sections) {
        return new PathDto(sections.getStations().stream()
                .map(StationDto::from)
                .collect(Collectors.toCollection(LinkedHashSet::new)), sections.getDistance());
    }

    public Set<StationDto> getStationDtos() {
        return stationDtos;
    }

    public int getDistance() {
        return distance;
    }
}
