package atdd.station.dto.station;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StationCreateRequestDto {
    private String name;

    @Builder
    public StationCreateRequestDto(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return Station.builder()
                .name(this.name)
                .build();
    }

    public static StationCreateRequestDto toDtoEntity(String name) {
        return StationCreateRequestDto.builder()
                .name(name)
                .build();

    }
}
