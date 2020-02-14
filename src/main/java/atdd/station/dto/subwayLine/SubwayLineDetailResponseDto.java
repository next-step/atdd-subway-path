package atdd.station.dto.subwayLine;

import atdd.station.domain.Station;
import atdd.station.domain.SubwayLine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubwayLineDetailResponseDto {
    private String name;
    private String startTime;
    private String endTime;
    private String intervalTime;
    private List<Station> stations;

    @Builder
    public SubwayLineDetailResponseDto(String name, String startTime, String endTime, String intervalTime, List<Station> stations) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = stations;
    }

    public static SubwayLineDetailResponseDto toDtoEntity(SubwayLine subwayLine) {
        return SubwayLineDetailResponseDto.builder()
                .name(subwayLine.getName())
                .startTime(subwayLine.getStartTime())
                .endTime(subwayLine.getEndTime())
                .intervalTime(subwayLine.getIntervalTime())
                .stations(subwayLine.getStations())
                .build();
    }
}
