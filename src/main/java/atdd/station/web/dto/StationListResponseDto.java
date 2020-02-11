package atdd.station.web.dto;

import atdd.station.domain.station.Station;
import lombok.Getter;

@Getter
public class StationListResponseDto {
    private Long id;
    private String name;

    public StationListResponseDto(Station entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
