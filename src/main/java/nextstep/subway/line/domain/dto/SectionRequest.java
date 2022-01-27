package nextstep.subway.line.domain.dto;

import lombok.Builder;
import lombok.Getter;
import nextstep.subway.line.domain.model.Distance;

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
