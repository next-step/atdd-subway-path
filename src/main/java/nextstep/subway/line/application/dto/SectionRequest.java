package nextstep.subway.line.application.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.domain.Distance;

@Getter
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Distance distance;

    protected SectionRequest() {
    }

    @Builder
    private SectionRequest(Long upStationId, Long downStationId, Distance distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
