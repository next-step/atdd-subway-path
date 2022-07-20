package nextstep.subway.applicaion.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @Builder
    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
