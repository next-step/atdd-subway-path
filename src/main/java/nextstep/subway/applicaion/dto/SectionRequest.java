package nextstep.subway.applicaion.dto;

import lombok.Getter;

@Getter
public class SectionRequest {
    private final Long upStationId;
    private final Long downStationId;
    private final int distance;

    public SectionRequest(final Long upStationId, final Long downStationId, final int distance) {
        this.upStationId = upStationId;
        this.downStationId = downStationId;
        this.distance = distance;
    }
}
