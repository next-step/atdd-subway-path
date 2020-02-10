package atdd.station.web.dto;

import atdd.station.domain.station.Station;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class StationRequestDto {
    private String name;

    @Builder
    public StationRequestDto(String name){
        this.name = name;
    }

    public Station toEntity(){
        return Station.builder()
                .name(name)
                .build();
    }

}
