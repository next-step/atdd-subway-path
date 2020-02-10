package atdd.station.dto.station;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StationDetailResponseDto {
    private String name;

    @Builder
    public StationDetailResponseDto(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return Station.builder()
                .name(this.name)
                .build();
    }

    public static StationDetailResponseDto toDtoEntity(Station station) {
        return StationDetailResponseDto.builder()
                .name(station.getName())
                .build();
    }
}
