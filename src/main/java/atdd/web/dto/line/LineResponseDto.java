package atdd.web.dto.line;

import atdd.domain.stations.Line;
import atdd.domain.stations.Stations;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor
public class LineResponseDto {
    private Long id;
    private String name;
    private List<Stations> stations;
    private String startTime;
    private String endTime;
    private String intervalTime;

    @Builder
    public LineResponseDto(Long id, String name, List<Stations> stations, String startTime, String endTime, String intervalTime) {
        this.id = id;
        this.name = name;
        this.stations = stations;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
    }

    public LineResponseDto toRealDto(Line line){
        return LineResponseDto.builder()
                .id(line.getId())
                .name(line.getName())
                .stations(line.getStations())
                .startTime(line.getStartTime())
                .endTime(line.getEndTime())
                .intervalTime(line.getIntervalTime())
                .build();
    }
}
