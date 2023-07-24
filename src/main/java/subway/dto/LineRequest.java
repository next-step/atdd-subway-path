package subway.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import subway.domain.Line;
import subway.domain.Station;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
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
