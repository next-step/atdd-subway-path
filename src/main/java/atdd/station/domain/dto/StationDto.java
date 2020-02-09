package atdd.station.domain.dto;

import atdd.station.domain.Station;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StationDto {
    private String name;

    public static StationDto of(Station station) {
        return StationDto.builder()
                .name(station.getName())
                .build();
    }

    public static List<StationDto> listOf(List<Station> station) {
        return station.stream()
                .map(it -> StationDto.builder().name(it.getName()).build())
                .collect(Collectors.toList());
    }
}
