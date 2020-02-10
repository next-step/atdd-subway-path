package atdd.station.dto.subwayLine;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubwayLineDetailResponseDto {
    private String name;

    @Builder
    public SubwayLineDetailResponseDto(String name) {
        this.name = name;
    }

    public Station toEntity() {
        return Station.builder()
                .name(this.name)
                .build();
    }

    public static SubwayLineDetailResponseDto toDtoEntity(Station station) {
        return SubwayLineDetailResponseDto.builder()
                .name(station.getName())
                .build();
    }
}
