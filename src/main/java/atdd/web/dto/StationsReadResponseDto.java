package atdd.web.dto;

import atdd.domain.stations.Line;
import atdd.domain.stations.StationLine;
import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class StationsReadResponseDto {
    private Long id;
    private String name;
    private List<Line> lines;

    @Builder
    public StationsReadResponseDto(Long id, String name, List<Line> lines){
        this.id=id;
        this.name=name;
        this.lines=lines;
    }

    public StationsReadResponseDto toRealDto(Stations stations){
        return StationsReadResponseDto.builder()
                .id(id)
                .name(name)
                .lines(stations.getLines())
                .build();
    }
}
