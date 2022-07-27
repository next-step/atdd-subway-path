package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @Builder
    public LineRequest(final String name, final String color, final Long upStationId, final Long downStationId, final int distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
