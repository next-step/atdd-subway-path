package nextstep.subway.line.domain.dto;

import java.util.Objects;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.domain.model.Distance;

@Getter
public class LineRequest {
    private String name;
    private String color;
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    private LineRequest() {
    }

    @Builder
    private LineRequest(String name, String color, Long upStationId, Long downStationId, Distance distance) {
        this.name = name;
        this.color = color;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }

    public boolean hasSection() {
        return Objects.nonNull(upStationId) && Objects.nonNull(downStationId) && Objects.nonNull(distance);
    }
}
