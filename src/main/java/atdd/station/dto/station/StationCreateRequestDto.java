package atdd.station.dto.station;

import atdd.station.domain.Station;
import atdd.station.domain.Subway;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StationCreateRequestDto {
    private String name;
    private List<Subway> subways;

    @Builder
    public StationCreateRequestDto(String name, List<Subway> subways) {
        this.name = name;
        this.subways = subways;
    }

    public Station toEntity() {
        return Station.builder()
                .name(this.name)
                .subways(this.subways)
                .build();
    }

    public static StationCreateRequestDto toDtoEntity(String name, List<Subway> subways) {
        return StationCreateRequestDto.builder()
                .name(name)
                .subways(subways)
                .build();
    }
}
