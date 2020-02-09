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
    private Long id;
    private String name;

    public static StationDto of(Station station) {
        return StationDto.builder()
                .id(station.getId())
                .name(station.getName())
                .build();
    }

    public static List<StationDto> listOf(List<Station> station) {
        return station.stream()
                .map(it -> StationDto.builder()
                        .id(it.getId())
                        .name(it.getName()).build())
                .collect(Collectors.toList());
    }
}
