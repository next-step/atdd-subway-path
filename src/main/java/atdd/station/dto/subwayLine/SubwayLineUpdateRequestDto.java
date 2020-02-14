package atdd.station.dto.subwayLine;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubwayLineUpdateRequestDto {
    public List<Station> stations;

    @Builder
    public SubwayLineUpdateRequestDto(List<Station> stations) {
        this.stations = stations;
    }

    public static SubwayLineUpdateRequestDto toDtoEntity(List<Station> stations) {
        return SubwayLineUpdateRequestDto.builder()
                .stations(stations)
                .build();
    }

    public int getListDtoSize() {
        return this.stations.size();
    }

    public List<Station> toEntity() {
        return this.stations;
    }
}
