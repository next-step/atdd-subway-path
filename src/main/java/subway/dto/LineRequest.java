package subway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import subway.domain.Line;
import subway.domain.Station;

@Builder
@Getter
@AllArgsConstructor
public class LineRequest {

    private String name;

    private String color;

    private Long upStationId;

    private Long downStationId;

    private Long distance;

    public Line toLine(Station upStation, Station downStation) {
        return Line.builder()
            .name(this.name)
            .color(this.color)
            .distance(this.distance)
            .upStation(upStation)
            .downStation(downStation)
            .build();
    }

}
