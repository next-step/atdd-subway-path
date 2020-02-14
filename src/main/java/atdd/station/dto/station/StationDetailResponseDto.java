package atdd.station.dto.station;

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
public class StationDetailResponseDto {
    private String name;
    private List<SubwayLine> subwayLines;

    @Builder
    public StationDetailResponseDto(String name, List<SubwayLine> subwayLines) {
        this.name = name;
        this.subwayLines = subwayLines;
    }

    public static StationDetailResponseDto toDtoEntity(Station station) {
        return StationDetailResponseDto.builder()
                .name(station.getName())
                .subwayLines(station.getSubwayLines())
                .build();
    }
}
