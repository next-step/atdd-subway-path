package atdd.web.dto;

import atdd.domain.stations.Stations;
import lombok.Getter;

@Getter
public class StationsResponseDto {
    private Long id;
    private String name;

    public StationsResponseDto(Stations entity){
        this.id=entity.getId();
        this.name=entity.getName();
    }
}
