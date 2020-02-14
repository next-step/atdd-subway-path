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
public class StationCreateResponseDto {
    private long id;
    private String name;
    private List<SubwayLine> subwayLines;

    @Builder
    public StationCreateResponseDto(long id, String name, List<SubwayLine> subwayLines) {
        this.id = id;
        this.name = name;
        this.subwayLines = subwayLines;
    }

    public static StationCreateResponseDto toDtoEntity(Station station) {
        return StationCreateResponseDto.builder()
                .id(station.getId())
                .name(station.getName())
                .subwayLines(station.getSubwayLines())
                .build();
    }
}
