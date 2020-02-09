package atdd.web.dto;

import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StationsSaveRequestDto {
    private String name;

    @Builder
    public StationsSaveRequestDto(String name) {
        this.name = name;
    }

    public Stations toEntity(){
        return Stations.builder()
                .name(name)
                .build();
    }
}
