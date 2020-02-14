package atdd.station.dto.station;

import atdd.station.domain.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StationListResponseDto {
    public List<Station> stations;

    @Builder
    public StationListResponseDto(List<Station> stations) {
        this.stations = stations;
    }

    public static StationListResponseDto toDtoEntity(List<Station> stations) {
        return StationListResponseDto.builder()
                .stations(stations)
                .build();
    }

    public int getListDtoSize() {
        return this.stations.size();
    }

    @Override
    public String toString() {
        return "StationListResponseDto{" +
                "stations=" + stations.get(0).getName() +
                '}';
    }
}
