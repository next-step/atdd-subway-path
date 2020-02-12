package atdd.web.dto.line;

import atdd.domain.stations.Line;
import atdd.domain.stations.StationLine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class LineCreateRequestDto {
    private String name;
    private List<StationLine> stationLines;

    @Builder
    public LineCreateRequestDto(String name, List<StationLine> stationLines) {
        this.name = name;
        this.stationLines=stationLines;
    }

    public Line toEntity(){
        return Line.builder()
                .name(name)
                .stationLines(stationLines)
                .build();
    }
}
