package nextstep.subway.line.application.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Getter
@NoArgsConstructor
public class SectionRequest {
    @Min(1)
    private long upStationId;
    @Min(1)
    private long downStationId;
    @Min(0)
    private int distance;

    public SectionRequest(Long upStationId, Long downStationId, int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
