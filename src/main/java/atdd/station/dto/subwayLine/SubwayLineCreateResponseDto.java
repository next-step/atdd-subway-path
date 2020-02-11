package atdd.station.dto.subwayLine;

import atdd.station.domain.Station;
import atdd.station.domain.Subway;
import atdd.station.domain.SubwayLine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SubwayLineCreateResponseDto {
    private long id;
    private String name;
    private String startTime;
    private String endTime;
    private String intervalTime;
    private List<Station> stations;

    @Builder
    public SubwayLineCreateResponseDto(long id, String name, String startTime, String endTime, String intervalTime, List<Station> stations) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.intervalTime = intervalTime;
        this.stations = stations;
    }

    public static SubwayLineCreateResponseDto toDtoEntity(SubwayLine subwayLine) {
        return SubwayLineCreateResponseDto.builder()
                .id(subwayLine.getId())
                .name(subwayLine.getName())
                .startTime(subwayLine.getStartTime())
                .endTime(subwayLine.getEndTime())
                .intervalTime(subwayLine.getIntervalTime())
                .stations(subwayLine.getStations())
                .build();
    }
}
