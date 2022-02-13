package nextstep.subway.applicaion.dto;

import lombok.Builder;

@Builder
public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public Long getUpStationId() {
        return upStationId;
    }

    public Long getDownStationId() {
        return downStationId;
    }

    public int getDistance() {
        return distance;
    }
}
