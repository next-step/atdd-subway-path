package nextstep.subway.applicaion.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import nextstep.subway.domain.Line;

@Getter
@NoArgsConstructor
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public Line to() {
        return new Line(name, color);
    }
}
