package atdd.web.dto;

import atdd.domain.stations.StationLine;
import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StationsSaveRequestDto {
    private String name;
    private List<StationLine> stationLines;

    @Builder
    public StationsSaveRequestDto(String name, List<StationLine> stationLines) {
        this.name = name;
        this.stationLines=stationLines;
    }

    public Stations toEntity(){
        return Stations.builder()
                .name(name)
                .stationLines(stationLines)
                .build();
    }
}
