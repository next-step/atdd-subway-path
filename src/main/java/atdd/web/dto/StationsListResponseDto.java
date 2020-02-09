package atdd.web.dto;

import atdd.domain.stations.Stations;
import lombok.Getter;

@Getter
public class StationsListResponseDto {
    private Long id;
    private String name;

    public StationsListResponseDto(Stations entity){
        this.id=entity.getId();
        this.name=entity.getName();
    }

}
