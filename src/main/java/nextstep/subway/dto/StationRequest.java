package nextstep.subway.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StationRequest {
    private String name;
    private Long lineId;

    public String getName() {
        return name;
    }

    public Long getLineId() {
        return lineId;
    }
}
