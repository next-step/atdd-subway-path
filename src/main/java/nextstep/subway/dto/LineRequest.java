package nextstep.subway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.subway.domain.Line;
import nextstep.subway.domain.Station;

@Getter
@AllArgsConstructor
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    public Line toEntity(Station upStation, Station downStation) {
        return Line.of(name, color, upStation, downStation, distance);
    }
}
