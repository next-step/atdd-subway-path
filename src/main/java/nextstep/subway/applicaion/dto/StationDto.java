package nextstep.subway.applicaion.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Station;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationDto {
    private Long id;
    private String name;

    public static StationDto from(Station station) {
        return new StationDto(station.getId(), station.getName());
    }

    public static List<StationDto> from(List<Station> stations) {
        return stations.stream()
                       .map(StationDto::from)
                       .collect(Collectors.toList());
    }

    public static List<Station> toEntity(List<StationDto> stations) {
        return stations.stream().map(StationDto::toEntity).collect(Collectors.toList());
    }

    public static Station toEntity(StationDto station) {
        return new Station(station.getId(), station.getName());
    }
}
