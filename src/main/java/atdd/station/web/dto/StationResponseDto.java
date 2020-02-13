package atdd.station.web.dto;

import atdd.station.domain.station.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StationResponseDto {
    private Long id;
    private String name;

    public StationResponseDto(Station entity){
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
