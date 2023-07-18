package subway.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private Long distance;

    @Builder
    public SectionRequest(Long upStationId, Long downStationId, Long distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
