package nextstep.subway.line.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SectionRequest {
    private Long upStationId;
    private Long downStationId;
    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
