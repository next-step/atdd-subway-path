package atdd.web.dto;

import atdd.domain.stations.Line;
import atdd.domain.stations.StationLine;
import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class StationsResponseDto {
    private Long id;
    private String name;
    private List<Line> lines;

    @Builder
    public StationsResponseDto(Long id, String name, List<Line> lines){
        this.id=id;
        this.name=name;
        this.lines=lines;
    }

    public StationsResponseDto toRealDto(Stations stations){
        return StationsResponseDto.builder()
                .id(stations.getId())
                .name(stations.getName())
                .lines(stations.getLines())
                .build();
    }
}
