package atdd.path.domain.dto;

import atdd.path.domain.Station;
import lombok.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StationDto {
    private Long id;
    private String name;
    private Set<Item> lines;

    public static StationDto of(Station station) {
        return StationDto.builder()
                .id(station.getId())
                .name(station.getName())
                .lines(station.getLines().stream()
                        .map(it -> Item.of(it.getId(), it.getName()))
                        .collect(Collectors.toSet()))
                .build();
    }

    public static List<StationDto> listOf(List<Station> station) {
        return station.stream()
                .map(it -> StationDto.builder()
                        .id(it.getId())
                        .name(it.getName())
                        .lines(it.getLines().stream()
                                .map(it2 -> Item.of(it2.getId(), it2.getName()))
                                .collect(Collectors.toSet()))
                        .build())
                .collect(Collectors.toList());
    }

    public Station toStation() {
        return Station.builder()
                .name(this.name)
                .build();
    }
}
