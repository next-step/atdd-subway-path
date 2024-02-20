package nextstep.subway.domain.line.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSectionRequest {

    private Long downStationId;
    private Long upStationId;
    private int distance;

    @Builder
    private CreateSectionRequest(Long downStationId, Long upStationId, int distance) {
        this.downStationId = downStationId;
        this.upStationId = upStationId;
        this.distance = distance;
    }
}
