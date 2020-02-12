package atdd.web.dto;

import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StationsListResponseDto {
    public List<Stations> stations;

    @Builder
    public StationsListResponseDto(List<Stations> stationsList){
        this.stations=stationsList;
    }

    public StationsListResponseDto() {
    }

    public StationsListResponseDto toDtoEntity(List<Stations> stations) {
        return StationsListResponseDto.builder()
                .stationsList(stations)
                .build();
    }

}
