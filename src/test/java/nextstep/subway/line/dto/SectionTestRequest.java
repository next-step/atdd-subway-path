package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionTestRequest {
    private Long lineId;
    private Long upStationId;
    private Long downStationId;
    private int distance;

    @Builder
    private SectionTestRequest(Long lineId, Long upStationId, Long downStationId, int distance) {
        this.lineId = lineId;
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
